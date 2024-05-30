package woowacourse.shopping.data.repository.real

import android.util.Log
import woowacourse.shopping.data.remote.source.OrderDataSourceImpl
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.exception.LatchUtils.awaitOrThrow
import woowacourse.shopping.utils.exception.NoSuchDataException
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource = OrderDataSourceImpl(),
) : OrderRepository {
    override fun orderShoppingCart(ids: List<Int>) {
        val latch = CountDownLatch(1)
        var exception: Exception? = null

        thread {
            try {
                val response = orderDataSource.orderItems(ids = ids).execute()
                if (!response.isSuccessful) {
                    exception = NoSuchDataException()
                }
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)
    }
}
