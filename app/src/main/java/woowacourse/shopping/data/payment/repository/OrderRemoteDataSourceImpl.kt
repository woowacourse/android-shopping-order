package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.OrderRequestError
import woowacourse.shopping.data.payment.OrderRequestResult
import woowacourse.shopping.data.payment.dto.OrderRequestBody
import woowacourse.shopping.data.util.NetworkModule
import woowacourse.shopping.data.util.RetrofitService

class OrderRemoteDataSourceImpl(
    private val retrofitService: RetrofitService = NetworkModule.retrofitService,
) : OrderRemoteDataSource {
    override suspend fun requestOrder(orderCartIds: List<Int>): OrderRequestResult<Int> {
        return try {
            val response = retrofitService.requestOrder(OrderRequestBody(orderCartIds))
            return when {
                response.isSuccessful -> OrderRequestResult.Success(response.code())
                else -> {
                    OrderRequestResult.Error(OrderRequestError.Server(response.code(), response.message()))
                }
            }
        } catch (e: Exception) {
            OrderRequestResult.Error(OrderRequestError.Network)
        }
    }
}
