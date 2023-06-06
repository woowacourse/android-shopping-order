package woowacourse.shopping.data.datasource.remote.orderdetail

import woowacourse.shopping.data.datasource.local.AuthInfoDataSource
import woowacourse.shopping.data.datasource.remote.retrofit.ServicePool
import woowacourse.shopping.data.remote.request.OrderDTO
import java.util.concurrent.Executors

class OrderDetailSourceImpl(private val authInfoDataSource: AuthInfoDataSource) :
    OrderDetailSource {

    private val token = authInfoDataSource.getAuthInfo() ?: throw IllegalArgumentException()

    override fun getById(orderId: Long): Result<OrderDTO> {
        val executors = Executors.newSingleThreadExecutor()
        val result = executors.submit<Result<OrderDTO>> {
            val response = ServicePool.orderDataService.getById(token, orderId).execute()
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw IllegalArgumentException())
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executors.shutdown()
        return result
    }
}
