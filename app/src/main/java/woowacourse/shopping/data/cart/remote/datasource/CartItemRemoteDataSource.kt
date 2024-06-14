package woowacourse.shopping.data.cart.remote.datasource

import woowacourse.shopping.data.cart.remote.CartItemApiService
import woowacourse.shopping.data.cart.remote.dto.CartItemRequest
import woowacourse.shopping.data.cart.remote.dto.CartItemResponse
import woowacourse.shopping.data.common.ApiResponseHandler.handleApiResponse
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.ProductIdsCount

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    CartItemDataSource {
    override suspend fun fetchCartItems(): ResponseResult<CartItemResponse> = handleApiResponse { cartItemApiService.requestCartItems() }

    override suspend fun saveCartItem(productIdsCount: ProductIdsCount): ResponseResult<Unit> =
        handleApiResponse {
            val cartItemRequest = CartItemRequest(productIdsCount.productId, productIdsCount.quantity)
            cartItemApiService.addCartItem(cartItemRequest)
        }

    override suspend fun deleteCartItem(cartItemId: Long): ResponseResult<Unit> =
        handleApiResponse { cartItemApiService.removeCartItem(cartItemId) }

    override suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit> = handleApiResponse { cartItemApiService.updateCartItemQuantity(cartItemId, quantity) }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
