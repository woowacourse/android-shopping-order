package woowacourse.shopping.data.util

import retrofit2.HttpException
import woowacourse.shopping.BuildConfig

inline fun <T> safeApiCall(apiCall: () -> retrofit2.Response<T>): T =
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body() as T
        } else {
            throw HttpException(response)
        }
    } catch (exception: Exception) {
        if (BuildConfig.DEBUG) exception.printStackTrace()
        throw exception
    }
