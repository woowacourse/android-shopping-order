package woowacourse.shopping.data.util

import android.util.Log
import retrofit2.HttpException
import woowacourse.shopping.domain.error.NetworkError
import woowacourse.shopping.domain.error.NetworkExceptionWrapper
import java.io.IOException

suspend inline fun <T> safeApiCall(crossinline block: suspend () -> T): Result<T> =
    try {
        val result = block()
        Result.success(result)
    } catch (e: IOException) {
        Log.d("12345", e.toString())
        Result.failure(NetworkExceptionWrapper(NetworkError.Network))
    } catch (e: HttpException) {
        val error =
            when (e.code()) {
                401 -> NetworkError.Unauthorized
                404 -> NetworkError.NotFound
                else -> NetworkError.Server(e.code())
            }
        Log.d("12345", e.toString())
        Result.failure(NetworkExceptionWrapper(error))
    } catch (e: Exception) {
        Log.d("12345", e.toString())
        Result.failure(NetworkExceptionWrapper(NetworkError.Unknown(e)))
    }
