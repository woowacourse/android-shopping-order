package woowacourse.shopping.data.common

import retrofit2.HttpException
import retrofit2.Response

object HandleResponseResult {
    fun <T : Any> handleResponseResult(execute: () -> Response<T>): ResponseResult<T> {
        return try {
            val response = execute()
            val body = response.body()
            when {
                response.isSuccessful && body != null -> ResponseResult.Success(body)
                response.isSuccessful && response.code() == 204 -> ResponseResult.Success(Unit as T)
                else -> ResponseResult.Error(code = response.code(), message = response.message())
            }
        } catch (e: HttpException) {
            ResponseResult.Error(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            ResponseResult.Exception(e)
        }
    }
}
