package woowacourse.shopping.data

import android.util.Log
import retrofit2.HttpException
import woowacourse.shopping.domain.exception.NetworkError

class NetworkResultHandler {
    fun <T> handleException(error: Throwable): Result<T> {
        logErrorDetails(error)
        return Result.failure(handleNetworkError(error))
    }

    private fun logErrorDetails(throwable: Throwable) {
        when (throwable) {
            is HttpException -> {
                val code = throwable.code()
                val errorBody = throwable.response()?.errorBody()?.string()

                Log.e("API_ERROR", "[$code] HttpException cause $errorBody")
            }

            else -> {
                Log.e("API_ERROR", "Throwable: ${throwable::class.java.simpleName}")
                Log.e("API_ERROR", "message: ${throwable.message}")
            }
        }
    }

    private fun handleNetworkError(e: Throwable): NetworkError {
        return when (e) {
            is NetworkError.MissingLocationHeaderError -> e
            is HttpException -> {
                when (e.code()) {
                    400 -> NetworkError.HttpError.BadRequestError
                    401 -> NetworkError.HttpError.AuthenticationError
                    403 -> NetworkError.HttpError.AuthorizationError
                    404 -> NetworkError.HttpError.NotFoundError
                    else -> NetworkError.HttpError.ServerError
                }
            }

            else -> NetworkError.UnknownError
        }
    }
}
