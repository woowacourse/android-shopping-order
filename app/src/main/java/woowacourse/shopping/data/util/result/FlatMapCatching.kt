package woowacourse.shopping.data.util.result

import woowacourse.shopping.BuildConfig

inline fun <T, R> Result<T>.flatMapCatching(transform: (T) -> Result<R>): Result<R> =
    fold(
        onSuccess = { runCatching { transform(it) }.getOrElse { Result.failure(it) } },
        onFailure = { Result.failure(it) },
    ).onFailure {
        if (BuildConfig.DEBUG) it.printStackTrace()
    }
