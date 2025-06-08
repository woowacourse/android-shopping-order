package woowacourse.shopping.data.network

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.Result

class ResponseCall<T>(
    private val callDelegate: Call<T>,
) : Call<Result<T>> {
    override fun enqueue(callback: Callback<Result<T>>) {
        callDelegate.enqueue(
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    val result: Result<T> =
                        when (response.code()) {
                            in 200..299 -> {
                                val body = response.body()
                                if (body != null) {
                                    Result.success(body)
                                } else {
                                    Result.failure(NullPointerException("Response body is null"))
                                }
                            }
                            in 400..499 -> {
                                Result.failure(Exception("Client error ${response.code()}: ${response.message()}"))
                            }
                            else -> {
                                Result.failure(Exception("Server error ${response.code()}: ${response.message()}"))
                            }
                        }

                    callback.onResponse(
                        this@ResponseCall,
                        Response.success(result),
                    )
                }

                override fun onFailure(
                    call: Call<T>,
                    t: Throwable,
                ) {
                    val result = Result.failure<T>(t)
                    callback.onResponse(
                        this@ResponseCall,
                        Response.success(result),
                    )
                    call.cancel()
                }
            },
        )
    }

    override fun clone(): Call<Result<T>> = ResponseCall(callDelegate.clone())

    override fun execute(): Response<Result<T>> = throw UnsupportedOperationException("ResponseCall does not support execute.")

    override fun isExecuted(): Boolean = callDelegate.isExecuted

    override fun cancel() = callDelegate.cancel()

    override fun isCanceled(): Boolean = callDelegate.isCanceled

    override fun request(): Request = callDelegate.request()

    override fun timeout(): Timeout = callDelegate.timeout()
}
