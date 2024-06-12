package woowacourse.shopping.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState

class ShoppingCartRepositoryImpl(context: Context) : ShoppingCartRepository {
    private val cartItemDao = CartItemDatabase.getInstance(context).cartItemDao()

    override suspend fun addCartItem(product: Product): Result<Unit> {
        return Result.runCatching {
            val addedCartItemId =
                cartItemDao.saveCartItem(CartItem(product = product).toCartItemEntity())
            if (addedCartItemId == ERROR_DATA_ID) throw NoSuchDataException()
        }
    }

    override suspend fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> {
        return try {
            val pagingData =
                cartItemDao.findPagingCartItem(offset, pagingSize).map { it.toCartItem() }
            if (pagingData.isEmpty()) {
                throw NoSuchDataException()
            }
            Result.success(pagingData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCartItem(itemId: Long) {
        val deleteId = cartItemDao.deleteCartItemById(itemId)
        if (deleteId == ERROR_DELETE_DATA_ID) throw NoSuchDataException()
    }

    override suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        return try {
            var cartItem: CartItem? = null
            cartItem = cartItemDao.findCartItemByProductId(productId)?.toCartItem()
            Result.success(
                CartItemResult(
                    cartItemId = cartItem?.id ?: DEFAULT_CART_ITEM_ID,
                    counter = cartItem?.product?.cartItemCounter ?: CartItemCounter(),
                ),
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ): Result<UpdateCartItemResult> {
        return try {
            val cartItemResult = getCartItemResultFromProductId(product.id).getOrThrow()

            when (updateCartItemType) {
                UpdateCartItemType.INCREASE -> {
                    if (cartItemResult.cartItemId == DEFAULT_CART_ITEM_ID) {
                        return Result.success(UpdateCartItemResult.ADD)
                    } else {
                        cartItemResult.increaseCount()
                    }
                }

                UpdateCartItemType.DECREASE -> {
                    if (cartItemResult.decreaseCount() == ChangeCartItemResultState.Fail) {
                        deleteCartItem(cartItemResult.cartItemId)
                        return Result.success(UpdateCartItemResult.DELETE(cartItemResult.cartItemId))
                    }
                }

                is UpdateCartItemType.UPDATE -> {
                    cartItemResult.updateCount(updateCartItemType.count)
                }
            }

            val updateResult = runCatching {
                withContext(Dispatchers.IO) {
                    cartItemDao.updateCartItemCount(
                        cartItemResult.cartItemId,
                        cartItemResult.counter.itemCount,
                    )
                }
            }.getOrElse {
                throw NoSuchDataException()
            }

            if (updateResult == ERROR_UPDATE_DATA_ID) {
                throw NoSuchDataException()
            }

            Result.success(UpdateCartItemResult.UPDATED(cartItemResult))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getTotalCartItemCount(): Result<Int> {
        return Result.runCatching { cartItemDao.getTotalItemCount() }
    }

    companion object {
        const val ERROR_DATA_ID = -1L
        const val ERROR_DELETE_DATA_ID = 0
        const val ERROR_UPDATE_DATA_ID = 0
        const val DEFAULT_ITEM_SIZE = 0
    }
}
