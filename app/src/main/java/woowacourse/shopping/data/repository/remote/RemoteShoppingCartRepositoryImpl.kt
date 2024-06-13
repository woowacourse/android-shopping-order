package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.CartItemDataSourceImpl
import woowacourse.shopping.data.source.CartItemDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.DtoMapper.toCartItem
import woowacourse.shopping.utils.exception.AddCartException
import woowacourse.shopping.utils.exception.DeleteCartException
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.utils.exception.UpdateCartException
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState

class RemoteShoppingCartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = CartItemDataSourceImpl(),
) : ShoppingCartRepository {
    override suspend fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> =
        runCatching {
            val page = (offset + 1) / LOAD_SHOPPING_ITEM_SIZE
            val response = cartItemDataSource.loadCartItems(page = page, size = pagingSize)
            if (response.isSuccessful && response.body() != null) {
                response.body()?.cartItemDto?.map { it.toCartItem() } ?: emptyList()
            } else {
                throw NoSuchDataException()
            }
        }

    override suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> =
        runCatching {
            val response = cartItemDataSource.loadCartItems()
            if (response.isSuccessful && response.body() != null) {
                val cartItem =
                    response.body()?.cartItemDto?.find { it.product.id.toLong() == productId }
                        ?.toCartItem()
                CartItemResult(
                    cartItemId = cartItem?.id ?: CartItem.DEFAULT_CART_ITEM_ID,
                    counter = cartItem?.product?.cartItemCounter ?: CartItemCounter(),
                )
            } else {
                throw NoSuchDataException()
            }
        }

    override suspend fun increaseCartItem(product: Product): Result<Unit> =
        runCatching {
            val cartItemResult = getCartItemResultFromProductId(product.id).getOrThrow()

            if (cartItemResult.cartItemId == CartItem.DEFAULT_CART_ITEM_ID) {
                product.cartItemCounter.increase()
                insertCartItem(product).getOrThrow()
            } else {
                cartItemResult.increaseCount()
                product.updateCartItemCount(cartItemResult.counter.itemCount)
                updateCartCount(cartItemResult).getOrThrow()
            }
        }

    override suspend fun insertCartItem(product: Product): Result<Unit> =
        runCatching {
            val response =
                cartItemDataSource.addCartItem(
                    productId = product.id,
                    quantity = product.cartItemCounter.itemCount,
                )
            if (!response.isSuccessful) {
                throw AddCartException()
            }
        }

    override suspend fun decreaseCartItem(product: Product): Result<Unit> =
        runCatching {
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
        }

    override suspend fun deleteCartItem(itemId: Long): Result<Unit> =
        runCatching {
            val response = cartItemDataSource.deleteCartItem(id = itemId)
            if (!response.isSuccessful) {
                throw DeleteCartException()
            }
        }

    override suspend fun updateCartCount(cartItemResult: CartItemResult): Result<Unit> =
        runCatching {
            val response =
                cartItemDataSource.updateCartItem(
                    id = cartItemResult.cartItemId,
                    quantity = cartItemResult.counter.itemCount,
                )
            if (!response.isSuccessful) {
                throw UpdateCartException()
            }
        }

    override suspend fun getTotalCartItemCount(): Result<Int> =
        runCatching {
            val response = cartItemDataSource.loadCartItemCount()
            if (response.isSuccessful && response.body() != null) {
                val quantity = response.body()?.quantity ?: ERROR_QUANTITY_SIZE
                if (quantity == ERROR_QUANTITY_SIZE) throw NoSuchDataException()
                quantity
            } else {
                throw NoSuchDataException()
            }
        }

    companion object {
        private const val ERROR_QUANTITY_SIZE = -1
        const val LOAD_SHOPPING_ITEM_SIZE = 50
        const val LOAD_SHOPPING_ITEM_OFFSET = 0
        const val LOAD_RECOMMEND_ITEM_SIZE = 10
    }
}
