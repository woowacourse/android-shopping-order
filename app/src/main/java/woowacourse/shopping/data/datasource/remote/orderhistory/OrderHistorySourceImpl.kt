package woowacourse.shopping.data.datasource.remote.orderhistory

import woowacourse.shopping.data.datasource.local.AuthInfoDataSource
import woowacourse.shopping.data.datasource.remote.retrofit.ServicePool
import woowacourse.shopping.data.remote.request.OrderDTO
import java.util.concurrent.Executors

class OrderHistorySourceImpl(private val authInfoDataSource: AuthInfoDataSource) :
    OrderHistorySource {

    private val token = authInfoDataSource.getAuthInfo() ?: throw IllegalArgumentException()

    override fun getOrderList(): Result<List<OrderDTO>> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<List<OrderDTO>>> {
            val response = ServicePool.orderHistoryService.getOrderList(token).execute()
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw IllegalArgumentException())
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }
}
