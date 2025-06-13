package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.api.OrderApi
import woowacourse.shopping.data.di.ApiResult
import woowacourse.shopping.data.model.request.OrderProductsRequest

class OrderRemoteDataSourceImpl(
    private val api: OrderApi,
) : OrderRemoteDataSource {
    override suspend fun postOrderProducts(cartIds: List<Long>): ApiResult<Unit> =
        try {
            val request = OrderProductsRequest(cartIds)
            val response = api.postOrderProducts(request)
            when {
                response.isSuccessful -> ApiResult.Success(Unit)
                response.code() in 400..499 ->
                    ApiResult.ClientError(
                        response.code(),
                        response.message(),
                    )

                response.code() >= 500 -> ApiResult.ServerError(response.code(), response.message())
                else -> ApiResult.UnknownError
            }
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
}
