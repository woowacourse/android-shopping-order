package woowacourse.shopping.data.common

import retrofit2.HttpException
import retrofit2.Response

object ResponseHandlingUtils {
    fun <T : Any> handleExecute(execute: () -> Response<T>): ResponseResult<T> {
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

   fun <T : Any> handleResponse(response: ResponseResult<T>): T {
        return when (response) {
            is ResponseResult.Success -> response.data
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }
}
