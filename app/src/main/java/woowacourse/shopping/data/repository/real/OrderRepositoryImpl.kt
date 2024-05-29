package woowacourse.shopping.data.repository.real

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.source.OrderDataSourceImpl
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.exception.LatchUtils.awaitOrThrow
import woowacourse.shopping.utils.exception.NoSuchDataException
import java.util.concurrent.CountDownLatch

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource = OrderDataSourceImpl(),
): OrderRepository {
    override fun orderShoppingCart(ids: List<Int>) {
        val latch = CountDownLatch(1)
        var exception: Exception? = null

        orderDataSource.orderItems(ids = ids)
            .enqueue(object : Callback<Unit>{
                override fun onResponse(p0: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful){
                        exception = NoSuchDataException()
                    }
                    latch.countDown()
                }

                override fun onFailure(p0: Call<Unit>, t: Throwable) {
                    exception = Exception(t.message)
                    latch.countDown()
                }

            })
        latch.awaitOrThrow(exception)
    }
}
