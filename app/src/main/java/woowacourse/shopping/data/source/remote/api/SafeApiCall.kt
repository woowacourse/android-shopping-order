package woowacourse.shopping.data.source.remote.api

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import woowacourse.shopping.error.NetworkError
import woowacourse.shopping.error.Result
import java.io.IOException

suspend fun <T> safeNetworkApiCall(call: suspend () -> T): Result<T, NetworkError> =
    try {
        Result.Success(call())
    } catch (err: CancellationException) {
        throw err
    } catch (err: SerializationException) {
        Log.e("safeNetworkApiCall", "직렬화 오류", err)
        Result.Error(NetworkError.SerializationError)
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
