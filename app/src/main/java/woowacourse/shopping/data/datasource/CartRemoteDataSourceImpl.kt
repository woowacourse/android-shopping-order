package woowacourse.shopping.data.datasource

import retrofit2.Response
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.service.CartService
import woowacourse.shopping.domain.error.NetworkError
import woowacourse.shopping.domain.error.NetworkExceptionWrapper

class CartRemoteDataSourceImpl(
    private val cartService: CartService,
) : CartRemoteDataSource {
    override suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ): PageableResponse<CartItemResponse> = cartService.fetchCartItems(AUTHORIZATION_KEY, page, size)

    override suspend fun addCartItem(addCartItemCommand: AddCartItemCommand): Long {
        val response = cartService.addCartItem(AUTHORIZATION_KEY, addCartItemCommand)
        return response.extractCartItemId()
    }

    override suspend fun deleteCartItem(cartId: Long) = cartService.deleteCartItem(AUTHORIZATION_KEY, cartId)

    override suspend fun patchCartItemQuantity(
        cartId: Long,
        quantity: Quantity,
    ) = cartService.patchCartItemQuantity(AUTHORIZATION_KEY, cartId, quantity)

    override suspend fun fetchCartItemCount(): Quantity = cartService.fetchCartItem(AUTHORIZATION_KEY)

    private fun Response<*>.extractCartItemId(): Long {
        val locationHeader = this.headers()[HEADER_LOCATION]
        return locationHeader
            ?.substringAfter(HEADER_CART_ID_PREFIX)
            ?.takeWhile { it.isDigit() }
            ?.toLongOrNull()
            ?: throw NetworkExceptionWrapper(NetworkError.NotFound)
    }

    companion object {
        private const val ADD_CART_PRODUCT_FAILURE_MESSAGE = "%s 상품을 장바구니에 상품을 추가하지 못했습니다."
        private const val AUTHORIZATION_KEY = "Basic ${BuildConfig.AUTHORIZATION_KEY}"
        private const val HEADER_LOCATION = "Location"
        private const val HEADER_CART_ID_PREFIX = "/cart-items/"
    }
}
