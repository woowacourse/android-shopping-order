package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.data.api.CartApi
import woowacourse.shopping.data.di.ApiResult
import woowacourse.shopping.data.model.request.CartItemQuantityRequest
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemsQuantityResponse
import woowacourse.shopping.data.model.response.CartItemsResponse

class CartRemoteDataSourceImpl(
    private val api: CartApi,
) : CartRemoteDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): ApiResult<CartItemsResponse> =
        try {
            val response = api.getCartItems(page, size)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body)
                } else {
                    ApiResult.UnknownError
                }
            } else {
                when (response.code()) {
                    in 400..499 -> ApiResult.ClientError(response.code(), response.message())
                    in 500..599 -> ApiResult.ServerError(response.code(), response.message())
                    else -> ApiResult.UnknownError
                }
            }
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }

    override suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ): ApiResult<Unit> =
        try {
            val request = CartItemRequest(productId = productId, quantity = quantity)
            val response = api.postCartItem(request)

            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                when (response.code()) {
                    in 400..499 -> ApiResult.ClientError(response.code(), response.message())
                    in 500..599 -> ApiResult.ServerError(response.code(), response.message())
                    else -> ApiResult.UnknownError
                }
            }
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }

    override suspend fun deleteCartItem(cartId: Long): ApiResult<Unit> =
        try {
            val response = api.deleteCartItem(cartId)
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                when (response.code()) {
                    in 400..499 -> ApiResult.ClientError(response.code(), response.message())
                    in 500..599 -> ApiResult.ServerError(response.code(), response.message())
                    else -> ApiResult.UnknownError
                }
            }
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }

    override suspend fun patchCartItem(
        cartId: Long,
        quantity: Int,
    ): ApiResult<Unit> =
        try {
            val request = CartItemQuantityRequest(quantity)
            val response = api.patchCartItem(cartId, request)

            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                when (response.code()) {
                    in 400..499 -> ApiResult.ClientError(response.code(), response.message())
                    in 500..599 -> ApiResult.ServerError(response.code(), response.message())
                    else -> ApiResult.UnknownError
                }
            }
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }

    override suspend fun getCartItemsCount(): ApiResult<CartItemsQuantityResponse> =
        try {
            val response = api.getCartItemsCount()

            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.UnknownError
            } else {
                when (response.code()) {
                    in 400..499 -> ApiResult.ClientError(response.code(), response.message())
                    in 500..599 -> ApiResult.ServerError(response.code(), response.message())
                    else -> ApiResult.UnknownError
                }
            }
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
}
