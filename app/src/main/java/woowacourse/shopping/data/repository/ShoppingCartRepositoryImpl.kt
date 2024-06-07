package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.cartItem.CartItemDatabase
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.EntityMapper.toCartItem
import woowacourse.shopping.utils.EntityMapper.toCartItemEntity
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState

class ShoppingCartRepositoryImpl(context: Context) : ShoppingCartRepository {
    private val cartItemDao = CartItemDatabase.getInstance(context).cartItemDao()

    override suspend fun insertCartItem(product: Product): Result<Unit> {
        return try {
            val addedCartItemId =
                cartItemDao.saveCartItem(CartItem(product = product).toCartItemEntity())
            if (addedCartItemId == ERROR_DATA_ID) throw NoSuchDataException()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> {
        return try {
            val pagingData =
                cartItemDao.findPagingCartItem(offset, pagingSize).map { it.toCartItem() }
            if (pagingData.isEmpty()) throw NoSuchDataException()
            Result.success(pagingData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCartItem(itemId: Long): Result<Unit> {
        return try {
            val deleteId = cartItemDao.deleteCartItemById(itemId)
            if (deleteId == ERROR_DELETE_DATA_ID) throw NoSuchDataException()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun increaseCartItem(product: Product): Result<Unit> {
        return try {
            val cartItemResult = getCartItemResultFromProductId(product.id).getOrThrow()
            if (cartItemResult.cartItemId == CartItem.DEFAULT_CART_ITEM_ID) {
                insertCartItem(product).getOrThrow()
            } else {
                cartItemResult.increaseCount()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun decreaseCartItem(product: Product): Result<Unit> {
        return try {
            val cartItemResult = getCartItemResultFromProductId(product.id).getOrThrow()
            if (cartItemResult.cartItemId == CartItem.DEFAULT_CART_ITEM_ID) {
                throw NoSuchDataException()
            } else {
                if (cartItemResult.decreaseCount() == ChangeCartItemResultState.Fail) {
                    deleteCartItem(cartItemResult.cartItemId).getOrThrow()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCartCount(cartItemResult: CartItemResult): Result<Unit> {
        return try {
            val cartItem = cartItemDao.findCartItemById(cartItemResult.cartItemId)
            if (cartItem == null) {
                throw NoSuchDataException()
            } else {
                cartItem.product.cartItemCounter.updateCount(cartItemResult.counter.itemCount)
                cartItemDao.updateCartItemCount(
                    cartItem.id,
                    cartItem.product.cartItemCounter.itemCount,
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        return try {
            val cartItem = cartItemDao.findCartItemByProductId(productId)?.toCartItem()
            val cartItemResult =
                CartItemResult(
                    cartItemId = cartItem?.id ?: CartItem.DEFAULT_CART_ITEM_ID,
                    counter = cartItem?.product?.cartItemCounter ?: CartItemCounter(),
                )
            Result.success(cartItemResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTotalCartItemCount(): Result<Int> {
        return try {
            Result.success(cartItemDao.getTotalItemCount())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val ERROR_DATA_ID = -1L
        const val ERROR_DELETE_DATA_ID = 0
        const val DEFAULT_ITEM_SIZE = 0
    }
}
