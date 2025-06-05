package woowacourse.shopping.data.datasource.remote

import retrofit2.Response
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.service.CartService
import woowacourse.shopping.data.util.safeApiCall

class CartRemoteDataSourceImpl(
    private val cartService: CartService,
) : CartRemoteDataSource {
    override suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ): Result<PageableResponse<CartItemResponse>> =
        safeApiCall {
            cartService.fetchCartItems(
                AUTHORIZATION_KEY,
                page,
                size,
            )
        }

    override suspend fun addCartItem(addCartItemCommand: AddCartItemCommand): Result<Long> =
        runCatching {
            val response = cartService.addCartItem(AUTHORIZATION_KEY, addCartItemCommand)
            val cartId = response.extractCartItemId()
            requireNotNull(cartId) { ADD_CART_PRODUCT_FAILURE_MESSAGE.format(addCartItemCommand.productId) }
        }

    override suspend fun deleteCartItem(cartId: Long): Result<Unit> =
        safeApiCall {
            cartService.deleteCartItem(
                AUTHORIZATION_KEY,
                cartId,
            )
        }

    override suspend fun patchCartItemQuantity(
        cartId: Long,
        quantity: Quantity,
    ): Result<Unit> =
        safeApiCall {
            cartService.patchCartItemQuantity(AUTHORIZATION_KEY, cartId, quantity)
        }

    override suspend fun fetchCartItemCount(): Result<Quantity> =
        safeApiCall {
            cartService.fetchCartItem(
                AUTHORIZATION_KEY,
            )
        }

    private fun Response<*>.extractCartItemId(): Long? {
        val locationHeader = this.headers()[HEADER_LOCATION]
        return locationHeader
            ?.substringAfter(HEADER_CART_ID_PREFIX)
            ?.takeWhile { it.isDigit() }
            ?.toLongOrNull()
    }

    companion object {
        private const val ADD_CART_PRODUCT_FAILURE_MESSAGE = "%s 상품을 장바구니에 상품을 추가하지 못했습니다."
        private const val AUTHORIZATION_KEY = "Basic ${BuildConfig.AUTHORIZATION_KEY}"
        private const val HEADER_LOCATION = "Location"
        private const val HEADER_CART_ID_PREFIX = "/cart-items/"
    }
}
