package woowacourse.shopping.data.remote.api

import java.lang.RuntimeException

inline fun <T : Any?> ApiResponse<T>.result(): T = when (this) {
    is ApiResponse.Success -> data
    is ApiResponse.Error -> throw RuntimeException(message)
    is ApiResponse.Exception -> throw e
}


inline fun <T : Any?> ApiResponse<T>.resultOrNull(): T? = when (this) {
    is ApiResponse.Success -> data
    is ApiResponse.Error -> null
    is ApiResponse.Exception -> null
}
