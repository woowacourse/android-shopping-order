package woowacourse.shopping.data

import woowacourse.shopping.BuildConfig

inline fun <T> handleResult(action: () -> T): Result<T> =
    runCatching(action).onFailure { throwable ->
        if (BuildConfig.DEBUG) throwable.printStackTrace()
    }
