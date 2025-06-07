package woowacourse.shopping.data.datasource

import retrofit2.Response
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemResponse
import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.data.util.safeApiCall

class CartItemDataSourceImpl(
    private val cartItemService: CartItemService,
) : CartItemDataSource {
    override suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ): Result<CartItemResponse> =
        safeApiCall {
            cartItemService.getCartItems(page, size)
        }

    override suspend fun submitCartItem(cartItem: CartItemRequest): Result<Long> =
        runCatching {
            cartItemService.postCartItem(cartItem).extractCartItemId()
                ?: throw IllegalStateException("cartId가 null입니다")
        }

    override suspend fun removeCartItem(cartId: Long): Result<Unit> =
        safeApiCall {
            cartItemService.deleteCartItem(cartId)
        }

    override suspend fun updateCartItem(
        cartId: Long,
        quantity: Quantity,
    ): Result<Unit> =
        safeApiCall {
            cartItemService.patchCartItem(cartId, quantity)
        }

    override suspend fun fetchCartItemsCount(): Result<Quantity> =
        safeApiCall {
            cartItemService.getCartItemsCount()
        }

    private fun Response<*>.extractCartItemId(): Long? {
        val locationHeader = this.headers()[HEADER_LOCATION]
        return locationHeader
            ?.substringAfter(HEADER_CART_ID_PREFIX)
            ?.takeWhile { it.isDigit() }
            ?.toLongOrNull()
    }

    companion object {
        private const val HEADER_LOCATION = "Location"
        private const val HEADER_CART_ID_PREFIX = "/cart-items/"
    }
}
