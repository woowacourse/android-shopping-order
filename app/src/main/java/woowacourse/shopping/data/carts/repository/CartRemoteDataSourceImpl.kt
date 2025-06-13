package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.dto.CartItemRequest
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.util.NetworkModule
import woowacourse.shopping.data.util.RetrofitService
import woowacourse.shopping.data.util.api.ApiError
import woowacourse.shopping.data.util.api.ApiResult

class CartRemoteDataSourceImpl(
    private val retrofitService: RetrofitService = NetworkModule.retrofitService,
) : CartRemoteDataSource {
    override suspend fun fetchCartItemByOffset(
        limit: Int,
        offset: Int,
    ): ApiResult<CartResponse> = fetchCartItemByPage(offset / limit, limit)

    override suspend fun fetchCartItemByPage(
        page: Int,
        size: Int,
    ): ApiResult<CartResponse> =
        try {
            val response = retrofitService.requestCartProduct(page = page, size = size)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Network)
        }

    override suspend fun fetchAuthCode(validKey: String): ApiResult<Int> {
        try {
            val response = retrofitService.requestCartCounts()
            return when {
                response.isSuccessful -> ApiResult.Success(response.code())
                else -> ApiResult.Error(ApiError.Server(response.code(), response.message()))
            }
        } catch (e: Exception) {
            return ApiResult.Error(ApiError.Network)
        }
    }

    override suspend fun updateCartItemCount(
        cartId: Int,
        cartQuantity: CartQuantity,
    ): ApiResult<Int> {
        val response =
            retrofitService.updateCartCounts(
                cartId = cartId,
                requestBody = cartQuantity,
            )
        return if (response.isSuccessful) {
            ApiResult.Success(response.code())
        } else {
            if (response.code() == 400) {
                ApiResult.Error(ApiError.NotFound)
            } else {
                ApiResult.Error(
                    ApiError.Server(response.code(), response.message()),
                )
            }
        }
    }

    override suspend fun deleteItem(cartId: Int): ApiResult<Int> =
        try {
            val response = retrofitService.deleteCartItem(cartId = cartId)
            if (response.isSuccessful) {
                ApiResult.Success(response.code())
            } else {
                ApiResult.Error(
                    ApiError.Server(response.code(), response.message()),
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Network)
        }

    override suspend fun addItem(
        itemId: Int,
        itemCount: Int,
    ): ApiResult<AddItemResult> {
        try {
            val response =
                retrofitService
                    .addCartItem(
                        cartItem = CartItemRequest(itemId, itemCount),
                    )
            if (response.isSuccessful) {
                val location = response.headers()["Location"]
                val cartItemId = extractCartItemId(location)

                return ApiResult.Success(AddItemResult(response.code(), cartItemId))
            } else {
                return ApiResult.Error(
                    ApiError.Server(response.code(), response.message()),
                )
            }
        } catch (e: Exception) {
            return ApiResult.Error(ApiError.Network)
        }
    }

    private fun extractCartItemId(location: String?): Int =
        try {
            location?.substringAfterLast("/")?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            0
        }
}
