package woowacourse.shopping.data.datasource.remote.orderdetail

import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.OrderDTO
import java.util.concurrent.Executors

class OrderRemoteDetailSourceImpl :
    OrderRemoteDetailSource {

    override fun getById(orderId: Long): Result<OrderDTO> {
        val executors = Executors.newSingleThreadExecutor()
        val result = executors.submit<Result<OrderDTO>> {
            val response = RetrofitClient.getInstance().orderDataService.getById(orderId).execute()
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
