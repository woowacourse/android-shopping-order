package woowacourse.shopping.data.cart.datasource

import retrofit2.Response
import woowacourse.shopping.data.cart.model.CartPageData
import woowacourse.shopping.data.cart.toData
import woowacourse.shopping.remote.dto.request.CartItemRequest
import woowacourse.shopping.remote.dto.request.UpdateCartCountRequest
import woowacourse.shopping.remote.service.CartService

class DefaultCartDataSource(
    private val cartService: CartService,
) : CartDataSource {
    override suspend fun loadCarts(
        currentPage: Int,
        productSize: Int,
    ): Result<CartPageData> {
        return runCatching {
            cartService.fetchCartItems(currentPage, productSize)
                .toData()
        }
    }

    override suspend fun loadTotalCarts(): Result<CartPageData> {
        return runCatching {
            val totalCount = cartService.fetchCartItemCount()
            cartService.fetchCartItems(0, totalCount.quantity)
                .toData()
        }
    }

    override suspend fun createCartProduct(
        productId: Long,
        count: Int,
    ): Result<Long> {
        return runCatching {
            val request = CartItemRequest(productId, count)
            val response = cartService.createCartItems(request)
            val id = response.toIdOrNull() ?: error("Failed to create cart product")
            id
        }
    }

    override suspend fun updateCartCount(
        cartId: Long,
        count: Int,
    ): Result<Unit> {
        return runCatching {
            cartService.patchCartItem(cartId, UpdateCartCountRequest(count))
        }
    }

    override suspend fun deleteCartProduct(cartId: Long): Result<Unit> {
        return runCatching { cartService.deleteCartItem(cartId) }
    }

    private fun <T> Response<T>.toIdOrNull(): Long? {
        return headers()["LOCATION"]?.substringAfterLast("/")
            ?.toLongOrNull()
    }
}
