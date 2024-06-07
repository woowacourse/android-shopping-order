package woowacourse.shopping.remote

import retrofit2.HttpException
import retrofit2.Response

suspend fun <T> executeSafeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    val response = apiCall()
    return if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            NetworkResult.Success(body)
        } else {
            NetworkResult.Success(Unit as T)
        }
    } else {
        NetworkResult.Error(HttpException(response))
    }
}
