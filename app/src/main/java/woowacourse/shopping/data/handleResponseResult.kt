package woowacourse.shopping.data

import retrofit2.HttpException
import retrofit2.Response

fun <T : Any> handleResponseResult(
    execute: () -> Response<T>
): ResponseResult<T> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            ResponseResult.Success(body)
        } else {
            ResponseResult.Error(code = response.code(), message = response.message())
        }
    } catch (e: HttpException) {
        ResponseResult.Error(code = e.code(), message = e.message())
    } catch (e: Throwable) {
        ResponseResult.Exception(e)
    }
}