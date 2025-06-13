package woowacourse.shopping.util

import retrofit2.HttpException
import retrofit2.Response

inline fun <T> Response<T>.toResult(): Result<Response<T>> {
    return if (isSuccessful) {
        Result.success(this)
    } else {
        Result.failure(HttpException(this))
    }
}
