package woowacourse.shopping.utils.exception

import woowacourse.shopping.view.model.event.ErrorEvent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object LatchUtils {
    private const val WAIT_TIME_OUT = 10L
    private const val ERROR_THREAD_WAIT_TIME = "Thread was interrupted"

    fun executeWithLatch(action: () -> Unit) {
        val latch = CountDownLatch(1)
        var exception: Exception? = null
        thread {
            try {
                action()
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)
    }

    private fun CountDownLatch.awaitOrThrow(exception: Exception? = null) {
        try {
            this.await(WAIT_TIME_OUT, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            throw OrderException()
        }
        exception?.let { throw it }
    }
}
