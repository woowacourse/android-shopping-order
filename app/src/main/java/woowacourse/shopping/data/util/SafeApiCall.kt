package woowacourse.shopping.data.util

import retrofit2.HttpException
import woowacourse.shopping.BuildConfig

inline fun <T> safeApiCall(apiCall: () -> retrofit2.Response<T>): Result<T> =
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let { body ->
                Result.success(body)
            } ?: Result.failure(NullPointerException("SafeApiCall: response body is null"))
        } else {
            Result.failure(HttpException(response))
        }
    } catch (e: Exception) {
        if (BuildConfig.DEBUG) e.printStackTrace()
        Result.failure(e)
    }
