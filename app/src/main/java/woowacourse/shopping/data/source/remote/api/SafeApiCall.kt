package woowacourse.shopping.data.source.remote.api

import retrofit2.HttpException
import woowacourse.shopping.error.NetworkError
import woowacourse.shopping.error.Result
import java.io.IOException

suspend fun <T> safeNetworkApiCall(call: suspend () -> T): Result<T, NetworkError> =
    try {
        Result.Success(call())
    } catch (err: HttpException) {
        Result.Error(
            when (err.code()) {
                400 -> NetworkError.BadRequest
                401 -> NetworkError.Unauthorized
                403 -> NetworkError.Forbidden
                404 -> NetworkError.NotFound
                500 -> NetworkError.InternalServerError
                else -> NetworkError.UnknownError
            },
        )
    } catch (_: IOException) {
        Result.Error(NetworkError.IoError)
    }
