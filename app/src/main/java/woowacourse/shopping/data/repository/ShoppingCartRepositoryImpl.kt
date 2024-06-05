package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.cartItem.CartItemDatabase
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.EntityMapper.toCartItem
import woowacourse.shopping.utils.EntityMapper.toCartItemEntity
import woowacourse.shopping.utils.LatchUtils.executeWithLatch
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState

class ShoppingCartRepositoryImpl(context: Context) : ShoppingCartRepository {
    private val cartItemDao = CartItemDatabase.getInstance(context).cartItemDao()

    override fun addCartItem(product: Product): Result<Unit> {
        return executeWithLatch {
            val addedCartItemId = cartItemDao.saveCartItem(CartItem(product = product).toCartItemEntity())
            if (addedCartItemId == ERROR_DATA_ID) throw NoSuchDataException()
        }
    }

    override fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> {
        return executeWithLatch {
            val pagingData = cartItemDao.findPagingCartItem(offset, pagingSize).map { it.toCartItem() }
            if (pagingData.isEmpty()) throw NoSuchDataException()
            pagingData
        }
    }

    override fun deleteCartItem(itemId: Long): Result<Unit> {
        return executeWithLatch {
            val deleteId = cartItemDao.deleteCartItemById(itemId)
            if (deleteId == ERROR_DELETE_DATA_ID) throw NoSuchDataException()
        }
    }

    override fun increaseCartItem(product: Product): Result<Unit> {
        return executeWithLatch {
            val cartItemResult = getCartItemResultFromProductId(product.id).getOrThrow()
            if (cartItemResult.cartItemId == CartItem.DEFAULT_CART_ITEM_ID) {
                addCartItem(product).getOrThrow()
            } else {
                cartItemResult.increaseCount()
            }
        }
    }

    override fun decreaseCartItem(product: Product): Result<Unit> {
        return executeWithLatch {
            val cartItemResult = getCartItemResultFromProductId(product.id).getOrThrow()
            if (cartItemResult.cartItemId == CartItem.DEFAULT_CART_ITEM_ID) {
                throw NoSuchDataException()
            } else {
                if (cartItemResult.decreaseCount() == ChangeCartItemResultState.Fail) {
                    deleteCartItem(cartItemResult.cartItemId).getOrThrow()
                }
            }
        }
    }

    override fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        return executeWithLatch {
            val cartItem = cartItemDao.findCartItemByProductId(productId)?.toCartItem()
            CartItemResult(
                cartItemId = cartItem?.id ?: CartItem.DEFAULT_CART_ITEM_ID,
                counter = cartItem?.product?.cartItemCounter ?: CartItemCounter(),
            )
        }
    }

    // Todo: Refactor this function
    override fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ): Result<UpdateCartItemResult> {
        return executeWithLatch {
            val cartItemResult = getCartItemResultFromProductId(product.id).getOrThrow()
            when (updateCartItemType) {
                UpdateCartItemType.INCREASE -> {
                    if (cartItemResult.cartItemId == CartItem.DEFAULT_CART_ITEM_ID) {
                        return@executeWithLatch UpdateCartItemResult.ADD
                    } else {
                        cartItemResult.increaseCount()
                    }
                }
                UpdateCartItemType.DECREASE -> {
                    if (cartItemResult.decreaseCount() == ChangeCartItemResultState.Fail) {
                        deleteCartItem(cartItemResult.cartItemId).getOrThrow()
                        return@executeWithLatch UpdateCartItemResult.DELETE(cartItemResult.cartItemId)
                    }
                }
                is UpdateCartItemType.UPDATE -> {
                    cartItemResult.updateCount(updateCartItemType.count)
                }
            }
            val updateDataId = cartItemDao.updateCartItemCount(cartItemResult.cartItemId, cartItemResult.counter.itemCount)
            if (updateDataId == ERROR_UPDATE_DATA_ID) throw NoSuchDataException()
            UpdateCartItemResult.UPDATED(cartItemResult)
        }
    }

    override fun getTotalCartItemCount(): Result<Int> {
        return executeWithLatch {
            cartItemDao.getTotalItemCount()
        }
    }

    companion object {
        const val ERROR_DATA_ID = -1L
        const val ERROR_DELETE_DATA_ID = 0
        const val ERROR_UPDATE_DATA_ID = 0
        const val DEFAULT_ITEM_SIZE = 0
    }
}
