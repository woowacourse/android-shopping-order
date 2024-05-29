package woowacourse.shopping.utils.exception

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object LatchUtils {
    private const val WAIT_TIME_OUT = 10L
    private const val ERROR_THREAD_WAIT_TIME = "Thread was interrupted"

    fun CountDownLatch.awaitOrThrow(exception: Exception? = null) {
        try {
            this.await(WAIT_TIME_OUT, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            throw RuntimeException(ERROR_THREAD_WAIT_TIME, e)
        }
        exception?.let { throw it }
    }
}
