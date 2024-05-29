package woowacourse.shopping.data.util

import android.util.Log
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response

/***
 * execute 함수를 호출하여 Call 객체를 Result 객체로 변환하는 확장 함수
 *
 *
 * @param onSuccess 성공 시 실행할 람다
 * @param onError 실패 시 실행할 람다
 *
 * @return Result<T>
 */
fun <T> Call<T>.executeAsResult(): Result<T> {
    return try {
        val response: Response<T> = execute()

        if (response.isSuccessful) {
            val responseBody =
                response.body()
                    ?: return Result.failure(IllegalStateException("Response body is null"))
            Result.success(responseBody)
        } else {
            Result.failure(HttpException(response))
        }
    } catch (e: Exception) {
        when (e) {
            is HttpException -> {
                Log.e("HttpException", e.message())
                Result.failure(e)
            }

            else -> {
                Log.e("UNKNOWNException", e.stackTraceToString())
                Result.failure(e)
            }
        }
    }
}