package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.cartItem.CartItemDatabase
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItem.Companion.DEFAULT_CART_ITEM_ID
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.EntityMapper.toCartItem
import woowacourse.shopping.utils.EntityMapper.toCartItemEntity
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState
import woowacourse.shopping.view.model.event.ErrorEvent
import kotlin.concurrent.thread

class ShoppingCartRepositoryImpl(context: Context) : ShoppingCartRepository {
    private val cartItemDao = CartItemDatabase.getInstance(context).cartItemDao()

    override fun addCartItem(product: Product): Result<Unit> {
        thread {
            val addedCartItemId =
                cartItemDao.saveCartItem(CartItem(product = product).toCartItemEntity())
            if (addedCartItemId == ERROR_DATA_ID) throw ErrorEvent.AddCartEvent()
        }.join()
        return Result.success(Unit)
    }

    override fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> {
        var pagingData = emptyList<CartItem>()
        thread {
            pagingData = cartItemDao.findPagingCartItem(offset, pagingSize).map { it.toCartItem() }
        }.join()
        if (pagingData.isEmpty()) throw ErrorEvent.MaxPagingDataEvent()
        return Result.success(pagingData)
    }

    override fun deleteCartItem(itemId: Long): Result<Unit> {
        var deleteId = ERROR_DELETE_DATA_ID
        thread {
            deleteId = cartItemDao.deleteCartItemById(itemId)
        }.join()
        if (deleteId == ERROR_DELETE_DATA_ID) throw ErrorEvent.DeleteCartEvent()
        return Result.success(Unit)
    }

    override fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        var cartItem: CartItem? = null
        thread {
            cartItem = cartItemDao.findCartItemByProductId(productId)?.toCartItem()
        }.join()
        return Result.success(
            CartItemResult(
                cartItemId = cartItem?.id ?: DEFAULT_CART_ITEM_ID,
                counter = cartItem?.product?.cartItemCounter ?: CartItemCounter(),
            )
        )
    }

    override fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ): Result<UpdateCartItemResult> {
        val cartItemResult = getCartItemResultFromProductId(product.id).getOrThrow()
        when (updateCartItemType) {
            UpdateCartItemType.INCREASE -> {
                if (isValidCartId(cartItemResult.cartItemId)) {
                    return Result.success(UpdateCartItemResult.ADD)
                } else {
                    cartItemResult.increaseCount()
                }
            }

            UpdateCartItemType.DECREASE -> {
                if (isInValidDecreaseCount(cartItemResult.decreaseCount())) {
                    deleteCartItem(cartItemResult.cartItemId)
                    return Result.success(UpdateCartItemResult.DELETE(cartItemResult.cartItemId))
                }
            }

            is UpdateCartItemType.UPDATE -> cartItemResult.updateCount(updateCartItemType.count)
        }
        updateCartItemCount(cartItemResult)
        return Result.success(UpdateCartItemResult.UPDATED(cartItemResult))
    }

    private fun isValidCartId(id: Long): Boolean {
        return id == DEFAULT_CART_ITEM_ID
    }

    private fun isInValidDecreaseCount(changeCartItemResultState: ChangeCartItemResultState): Boolean {
        return changeCartItemResultState == ChangeCartItemResultState.Fail
    }

    private fun updateCartItemCount(cartItemResult: CartItemResult) {
        var updateDataId = ERROR_UPDATE_DATA_ID
        thread {
            updateDataId =
                cartItemDao.updateCartItemCount(
                    cartItemResult.cartItemId,
                    cartItemResult.counter.itemCount,
                )
        }.join()
        if (updateDataId == ERROR_UPDATE_DATA_ID) throw ErrorEvent.UpdateCartEvent()
    }

    override fun getTotalCartItemCount(): Result<Int>{
        var totalCount = 0
        thread {
            totalCount = cartItemDao.getTotalItemCount()
        }.join()
        return Result.success(totalCount)
    }

    companion object {
        const val ERROR_DATA_ID = -1L
        const val ERROR_DELETE_DATA_ID = 0
        const val ERROR_UPDATE_DATA_ID = 0
        const val DEFAULT_ITEM_SIZE = 0
    }
}
