package woowacourse.shopping.data.repository.real

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.source.OrderDataSourceImpl
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.exception.LatchUtils.awaitOrThrow
import woowacourse.shopping.utils.exception.NoSuchDataException
import java.util.concurrent.CountDownLatch

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource = OrderDataSourceImpl(NetworkManager.getApiClient()),
) : OrderRepository {
    override fun orderShoppingCart(ids: List<Int>) {
        val latch = CountDownLatch(1)
        var exception: Exception? = null

        orderDataSource.orderItems(ids = ids).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    exception = NoSuchDataException()
                }
                Log.d("OrderRepositoryImpl", "orderShoppingCart: ${response.code()}")
                latch.countDown()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                exception = t as? Exception ?: Exception(t)
                latch.countDown()
            }
        })

        latch.awaitOrThrow(exception)
    }
}
