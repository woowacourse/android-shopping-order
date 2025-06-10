package woowacourse.shopping.data.datasource.remote

import retrofit2.Response
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.service.CartService
import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.data.util.safeApiCall

class CartRemoteDataSourceImpl(
    private val cartService: CartService,
) : CartRemoteDataSource {
    override suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ): Result<PageableResponse<CartItemResponse>> =
        safeApiCall {
            cartService.fetchCartItems(page, size)
        }

    override suspend fun addCartItem(addCartItemCommand: AddCartItemCommand): Result<Long> =
        runCatchingDebugLog {
            val response = cartService.addCartItem(addCartItemCommand)
            val cartId = response.extractCartItemId()
            requireNotNull(cartId)
        }

    override suspend fun deleteCartItem(cartId: Long): Result<Unit> =
        safeApiCall {
            cartService.deleteCartItem(cartId)
        }

    override suspend fun patchCartItemQuantity(
        cartId: Long,
        quantity: Quantity,
    ): Result<Unit> =
        safeApiCall {
            cartService.patchCartItemQuantity(cartId, quantity)
        }

    override suspend fun fetchCartItemCount(): Result<Quantity> =
        safeApiCall {
            cartService.fetchCartItem()
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
