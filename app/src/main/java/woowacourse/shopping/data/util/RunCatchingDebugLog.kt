package woowacourse.shopping.data.util

import woowacourse.shopping.BuildConfig

inline fun <T> runCatchingDebugLog(block: () -> T): Result<T> = runCatching(block).onFailure { if (BuildConfig.DEBUG) it.printStackTrace() }
