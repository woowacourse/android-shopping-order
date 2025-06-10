package woowacourse.shopping.data.util.result

import woowacourse.shopping.BuildConfig

inline fun <T, R> Result<T>.mapCatchingDebugLog(transform: (T) -> R): Result<R> =
    mapCatching { transform(it) }.onFailure { if (BuildConfig.DEBUG) it.printStackTrace() }
