package woowacourse.shopping.data.remote.api

import java.lang.RuntimeException

inline fun <T : Any?> ApiResult<T>.result(): T = when (this) {
    is ApiResult.Success -> data
    is ApiResult.Error -> throw RuntimeException(message)
    is ApiResult.Exception -> throw e
}


inline fun <T : Any?> ApiResult<T>.resultOrNull(): T? = when (this) {
    is ApiResult.Success -> data
    is ApiResult.Error -> null
    is ApiResult.Exception -> null
}
