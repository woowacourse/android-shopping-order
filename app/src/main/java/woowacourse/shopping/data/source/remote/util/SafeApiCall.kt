package woowacourse.shopping.data.source.remote.util

import retrofit2.HttpException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> retrofit2.Response<T>
): Result<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            Result.success(response.body() as T)
        } else {
            Result.failure(HttpException(response))
        }
    } catch (e: HttpException) {
        Result.failure(e)
    }
}