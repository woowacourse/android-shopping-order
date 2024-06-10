package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.cartItem.CartItemDatabase
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItem.Companion.DEFAULT_CART_ITEM_ID
import woowacourse.shopping.domain.model.cart.CartItemCounter
import woowacourse.shopping.domain.model.cart.CartItemResult
import woowacourse.shopping.domain.model.cart.UpdateCartItemResult
import woowacourse.shopping.domain.model.cart.UpdateCartItemType
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.ErrorEvent
import woowacourse.shopping.utils.mapper.CartItemMapper.toCartItem
import woowacourse.shopping.utils.mapper.CartItemMapper.toCartItemEntity
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState

class ShoppingCartRepositoryImpl(context: Context) : ShoppingCartRepository {
    private val cartItemDao = CartItemDatabase.getInstance(context).cartItemDao()

    override suspend fun addCartItem(product: Product): Result<Unit> {
        return runCatching {
            cartItemDao.saveCartItem(CartItem(product = product).toCartItemEntity())
        }
    }

    override suspend fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> {
        return runCatching {
            val cartItems =
                cartItemDao.findPagingCartItem(offset, pagingSize).map { it.toCartItem() }
            if (cartItems.isEmpty()) throw ErrorEvent.MaxPagingDataEvent()
            cartItems
        }.recoverCatching {
            throw ErrorEvent.LoadDataEvent()
        }
    }

    override suspend fun deleteCartItem(itemId: Long): Result<Unit> {
        return runCatching {
            cartItemDao.deleteCartItemById(itemId)
        }
    }

    override suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        return runCatching {
            val cartItem = cartItemDao.findCartItemByProductId(productId)?.toCartItem()
            CartItemResult(
                cartItemId = cartItem?.id ?: throw ErrorEvent.LoadDataEvent(),
                counter = cartItem.product.cartItemCounter,
            )
        }.recoverCatching {
            CartItemResult(
                cartItemId = DEFAULT_CART_ITEM_ID,
                counter = CartItemCounter(),
            )
        }
    }

    override suspend fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ): Result<UpdateCartItemResult> {
        return getCartItemResultFromProductId(product.id)
            .mapCatching { cartItemResult ->
                when (updateCartItemType) {
                    UpdateCartItemType.INCREASE -> {
                        if (isValidCartId(cartItemResult.cartItemId)) {
                            addCartItemResult(product)
                        } else {
                            cartItemResult.increaseCount()
                            updateCartItemCount(cartItemResult)
                        }
                    }

                    UpdateCartItemType.DECREASE -> {
                        if (isInValidDecreaseCount(cartItemResult.decreaseCount())) {
                            deleteCartItemResult(cartItemResult)
                        } else {
                            cartItemResult.increaseCount()
                            updateCartItemCount(cartItemResult)
                        }
                    }

                    is UpdateCartItemType.UPDATE -> {
                        cartItemResult.updateCount(updateCartItemType.count)
                        updateCartItemCount(cartItemResult)
                    }
                }
            }
    }

    private fun isValidCartId(id: Long): Boolean {
        return id == DEFAULT_CART_ITEM_ID
    }

    private fun isInValidDecreaseCount(changeCartItemResultState: ChangeCartItemResultState): Boolean {
        return changeCartItemResultState == ChangeCartItemResultState.Fail
    }

    private suspend fun addCartItemResult(product: Product): UpdateCartItemResult {
        addCartItem(product).getOrNull() ?: throw ErrorEvent.AddCartEvent()
        return UpdateCartItemResult.ADD
    }

    private suspend fun updateCartItemCount(cartItemResult: CartItemResult): UpdateCartItemResult {
        return runCatching {
            cartItemDao.updateCartItemCount(
                itemId = cartItemResult.cartItemId,
                count = cartItemResult.counter.itemCount,
            )
            UpdateCartItemResult.UPDATED(cartItemResult)
        }.getOrElse {
            throw ErrorEvent.UpdateCartEvent()
        }
    }

    private suspend fun deleteCartItemResult(cartItemResult: CartItemResult): UpdateCartItemResult {
        deleteCartItem(cartItemResult.cartItemId).getOrNull()
            ?: throw ErrorEvent.DeleteCartEvent()
        return UpdateCartItemResult.DELETE(cartItemResult.cartItemId)
    }

    override suspend fun getTotalCartItemCount(): Result<Int> {
        return runCatching {
            cartItemDao.getTotalItemCount()
        }
    }

    companion object {
        const val DEFAULT_ITEM_SIZE = 0
    }
}
