package woowacourse.shopping.utils

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object LatchUtils {
    private const val WAIT_TIME_OUT = 10L
    private const val ERROR_THREAD_WAIT_TIME = "Thread was interrupted"

    private fun CountDownLatch.awaitOrThrow(exception: Exception? = null) {
        try {
            this.await(WAIT_TIME_OUT, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            throw RuntimeException(ERROR_THREAD_WAIT_TIME, e)
        }
        exception?.let { throw it }
    }

    fun <T> executeWithLatch(action: () -> T?): Result<T> {
        val latch = CountDownLatch(1)
        var result: T? = null
        var exception: Exception? = null
        thread {
            try {
                result = action()
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow()
        return exception?.let { Result.failure(it) } ?: Result.success(result!!)
    }
}
