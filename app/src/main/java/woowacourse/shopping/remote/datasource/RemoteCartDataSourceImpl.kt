package woowacourse.shopping.remote.datasource

import retrofit2.HttpException
import woowacourse.shopping.data.datasource.remote.RemoteCartDataSource
import woowacourse.shopping.remote.NetworkResult
import woowacourse.shopping.remote.api.ApiClient
import woowacourse.shopping.remote.api.CartApiService
import woowacourse.shopping.remote.dto.request.CartSaveRequest
import woowacourse.shopping.remote.dto.request.CartUpdateRequest
import woowacourse.shopping.remote.dto.response.CartQuantityResponse
import woowacourse.shopping.remote.dto.response.CartResponse
import woowacourse.shopping.remote.executeSafeApiCall

class RemoteCartDataSourceImpl(
    private val cartApiService: CartApiService =
        ApiClient.getApiClient().create(CartApiService::class.java),
) : RemoteCartDataSource {
    override suspend fun getCartItems(
        startPage: Int,
        pageSize: Int,
    ): NetworkResult<CartResponse> {
        return executeSafeApiCall { cartApiService.requestCartItems(page = startPage, size = pageSize) }
    }

    override suspend fun saveCartItem(
        productId: Long,
        quantity: Int,
    ): NetworkResult<Long> {
        val request = CartSaveRequest(productId, quantity)
        val response = cartApiService.requestAddCartItems(request)
        return if (response.isSuccessful) {
            val location = response.headers()["Location"]
            if (location != null) {
                val segments = location.split("/")
                val cartId = segments.last().toLong()
                NetworkResult.Success(cartId)
            } else {
                NetworkResult.Error(NullPointerException())
            }
        } else {
            NetworkResult.Error(HttpException(response))
        }
    }

    override suspend fun updateCartItemQuantity(
        cartId: Int,
        newQuantity: Int,
    ): NetworkResult<Unit> {
        val request = CartUpdateRequest(newQuantity)
        return executeSafeApiCall { cartApiService.requestUpdateCartItems(cartId, request) }
    }

    override suspend fun deleteCartItem(cartId: Int): NetworkResult<Unit> {
        return executeSafeApiCall { cartApiService.requestDeleteCartItems(cartId) }
    }

    override suspend fun getTotalCount(): NetworkResult<CartQuantityResponse> {
        return executeSafeApiCall { cartApiService.requestCartItemsCount() }
    }
}
