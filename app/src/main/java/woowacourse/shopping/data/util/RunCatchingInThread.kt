package woowacourse.shopping.data.util

import woowacourse.shopping.BuildConfig
import kotlin.concurrent.thread

inline fun <T> runCatchingInThread(
    crossinline onResult: (Result<T>) -> Unit = {},
    crossinline block: () -> T,
) {
    thread {
        val result =
            runCatching { block() }
                .onFailure { if (BuildConfig.DEBUG) it.printStackTrace() }
        onResult(result)
    }
}
