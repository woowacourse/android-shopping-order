package woowacourse.shopping.data.remote.api

import retrofit2.HttpException
import retrofit2.Response

suspend fun <T : Any?> handleApi(execute: suspend () -> Response<T>): ApiResponse<T> =
    try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            ApiResponse.Success(body)
        } else {
            ApiResponse.Error(code = response.code(), message = response.message())
        }
    } catch (e: HttpException) {
        ApiResponse.Error(code = e.code(), message = e.message())
    } catch (e: Throwable) {
        ApiResponse.Exception(e)
    }
