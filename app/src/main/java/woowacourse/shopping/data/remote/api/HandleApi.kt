package woowacourse.shopping.data.remote.api

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.ui.utils.exceptionHandler

suspend inline fun <T : Any?> handleApi(crossinline execute: suspend () -> Response<T>): ApiResponse<T> =
    coroutineScope {
        val deferredResult = async<ApiResponse<T>>(exceptionHandler()) {
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
        }
        return@coroutineScope deferredResult.await()
    }
