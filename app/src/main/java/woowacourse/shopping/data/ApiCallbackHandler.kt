package woowacourse.shopping.data

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.data.network.response.BaseResponse
import kotlin.text.split

class ApiCallbackHandler {
    fun <T> enqueueWithResult(
        call: Call<T>,
        callback: (Result<T>) -> Unit,
    ) {
        val result =
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        callback(Result.success(body))
                    } else if (response.isSuccessful) {
                        logMissingBody(call, response)
                        callback(Result.failure(NullPointerException()))
                    } else {
                        callback(Result.failure(HttpException(response)))
                    }
                }

                override fun onFailure(
                    call: Call<T>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            }

        call.enqueue(result)
    }

    fun <T : BaseResponse<R>, R> enqueueWithDomainTransform(
        call: Call<T>,
        callback: (Result<R>) -> Unit,
    ) {
        val result =
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        callback(Result.success(body.toDomain()))
                    } else if (response.isSuccessful) {
                        logMissingBody(call, response)
                        callback(Result.failure(NullPointerException()))
                    } else {
                        callback(Result.failure(HttpException(response)))
                    }
                }

                override fun onFailure(
                    call: Call<T>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            }

        call.enqueue(result)
    }

    fun <T> enqueueWithExtractHeaderLocationResult(
        call: Call<T>,
        callback: (Result<String>) -> Unit,
    ) {
        val result =
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    val location = extractHeaderLocation(response)

                    if (response.isSuccessful && location != null) {
                        callback(Result.success(location))
                    } else if (response.isSuccessful) {
                        logMissingHeader(call, response)
                        callback(Result.failure(NullPointerException()))
                    } else {
                        callback(Result.failure(HttpException(response)))
                    }
                }

                override fun onFailure(
                    call: Call<T>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            }

        call.enqueue(result)
    }

    private fun <T> extractHeaderLocation(response: Response<T>): String? {
        return response
            .headers()[HEADER_LOCATION]
            ?.split(URL_SEPARATOR)
            ?.last()
    }

    private fun <T> logMissingBody(
        call: Call<T>,
        response: Response<T>,
    ) {
        val request = call.request()
        Log.e(
            TAG,
            "Missing body: ${request.method} ${request.url} (${response.code()} ${response.message()})",
        )
    }

    private fun <T> logMissingHeader(
        call: Call<T>,
        response: Response<T>,
    ) {
        val request = call.request()
        Log.e(
            TAG,
            "Missing header $HEADER_LOCATION : ${request.method} ${request.url} (${response.code()} ${response.message()})",
        )
    }

    companion object {
        private const val TAG = "API"
        private const val HEADER_LOCATION = "Location"
        private const val URL_SEPARATOR = "/"
    }
}
