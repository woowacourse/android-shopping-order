package woowacourse.shopping.data.datasource.remote.orderhistory

import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.OrderDTO
import java.util.concurrent.Executors

class OrderHistoryRemoteSourceImpl :
    OrderHistoryRemoteSource {

    override fun getOrderList(): Result<List<OrderDTO>> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<List<OrderDTO>>> {
            val response = RetrofitClient.getInstance().orderDataService.getOrderList().execute()
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
