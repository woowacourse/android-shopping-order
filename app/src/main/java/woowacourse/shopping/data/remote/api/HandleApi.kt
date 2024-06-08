package woowacourse.shopping.data.remote.api

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.ui.utils.exceptionHandler

suspend inline fun <T : Any?> handleApi(crossinline execute: suspend () -> Response<T>): ApiResult<T> =
    coroutineScope {
        val deferredResult = async<ApiResult<T>>(exceptionHandler()) {
            try {
                val response = execute()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error(code = response.code(), message = response.message())
                }
            } catch (e: HttpException) {
                ApiResult.Error(code = e.code(), message = e.message())
            } catch (e: Throwable) {
                ApiResult.Exception(e)
            }
        }
        return@coroutineScope deferredResult.await()
    }
