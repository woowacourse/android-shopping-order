package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.dto.OrderRequestBody
import woowacourse.shopping.data.util.NetworkModule
import woowacourse.shopping.data.util.RetrofitService
import woowacourse.shopping.data.util.api.ApiError
import woowacourse.shopping.data.util.api.ApiResult

class OrderRemoteDataSourceImpl(
    private val retrofitService: RetrofitService = NetworkModule.retrofitService,
) : OrderRemoteDataSource {
    override suspend fun requestOrder(orderCartIds: List<Int>): ApiResult<Int> {
        return try {
            val response = retrofitService.requestOrder(OrderRequestBody(orderCartIds))
            return when {
                response.isSuccessful -> ApiResult.Success(response.code())
                else -> {
                    ApiResult.Error(ApiError.Server(response.code(), response.message()))
                }
            }
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Network)
        }
    }
}
