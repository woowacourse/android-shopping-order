package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.CartItemDataSourceImpl
import woowacourse.shopping.data.source.CartItemDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.DtoMapper.toCartItem
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState

class RemoteShoppingCartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = CartItemDataSourceImpl(),
) : ShoppingCartRepository {
    override suspend fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> {
        return try {
            val page = (offset + 1) / LOAD_SHOPPING_ITEM_SIZE
            val response = cartItemDataSource.loadCartItems(page = page, size = pagingSize)
            if (response.isSuccessful && response.body() != null) {
                val cartItems =
                    response.body()?.cartItemDto?.map { it.toCartItem() } ?: emptyList()
                Result.success(cartItems)
            } else {
                Result.failure(RuntimeException(response.code().toString()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        return try {
            val response = cartItemDataSource.loadCartItems()
            if (response.isSuccessful && response.body() != null) {
                val cartItem =
                    response.body()?.cartItemDto?.find { it.product.id.toLong() == productId }
                        ?.toCartItem()
                val cartItemResult =
                    CartItemResult(
                        cartItemId = cartItem?.id ?: CartItem.DEFAULT_CART_ITEM_ID,
                        counter = cartItem?.product?.cartItemCounter ?: CartItemCounter(),
                    )
                Result.success(cartItemResult)
            } else {
                Result.failure(RuntimeException(response.code().toString()))
            }
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
                product.updateCartItemCount(cartItemResult.counter.itemCount)
                updateCartCount(cartItemResult).getOrThrow()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertCartItem(product: Product): Result<Unit> {
        return try {
            product.cartItemCounter.increase()
            val response =
                cartItemDataSource.addCartItem(
                    productId = product.id,
                    quantity = product.cartItemCounter.itemCount,
                )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(RuntimeException(response.code().toString()))
            }
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
                } else {
                    updateCartCount(cartItemResult).getOrThrow()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCartItem(itemId: Long): Result<Unit> {
        return try {
            val response = cartItemDataSource.deleteCartItem(id = itemId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(NoSuchDataException())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCartCount(cartItemResult: CartItemResult): Result<Unit> {
        return try {
            val response =
                cartItemDataSource.updateCartItem(
                    id = cartItemResult.cartItemId,
                    quantity = cartItemResult.counter.itemCount,
                )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(RuntimeException(response.code().toString()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTotalCartItemCount(): Result<Int> {
        return try {
            val response = cartItemDataSource.loadCartItemCount()
            if (response.isSuccessful && response.body() != null) {
                val quantity = response.body()?.quantity ?: ERROR_QUANTITY_SIZE
                if (quantity == ERROR_QUANTITY_SIZE) throw NoSuchDataException()
                Result.success(quantity)
            } else {
                Result.failure(RuntimeException(response.code().toString()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val ERROR_QUANTITY_SIZE = -1
        const val LOAD_SHOPPING_ITEM_SIZE = 50
        const val LOAD_SHOPPING_ITEM_OFFSET = 0
        const val LOAD_RECOMMEND_ITEM_SIZE = 10
    }
}
