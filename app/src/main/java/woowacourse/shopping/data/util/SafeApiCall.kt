package woowacourse.shopping.data.util

import retrofit2.HttpException

inline fun <T> safeApiCall(apiCall: () -> retrofit2.Response<T>): Result<T> =
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            Result.success(
                response.body() ?: Unit as T,
            )
        } else {
            Result.failure(HttpException(response))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
