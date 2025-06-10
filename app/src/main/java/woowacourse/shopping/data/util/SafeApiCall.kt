package woowacourse.shopping.data.util

import retrofit2.HttpException
import woowacourse.shopping.BuildConfig

inline fun <T> safeApiCall(apiCall: () -> retrofit2.Response<T>): Result<T> =
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            Result.success(response.body() as T)
        } else {
            Result.failure(HttpException(response))
        }
    } catch (exception: Exception) {
        if (BuildConfig.DEBUG) exception.printStackTrace()
        Result.failure(exception)
    }
