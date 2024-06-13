package woowacourse.shopping.data.remote

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultCall<T : Any>(private val call: Call<T>) : Call<Result<T>> {
    override fun clone(): Call<Result<T>> = ResultCall(call.clone())

    override fun execute(): Response<Result<T>> {
        throw UnsupportedOperationException("ResultCall doesn't support execute")
    }

    override fun enqueue(callback: Callback<Result<T>>) {
        call.enqueue(
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    if (response.isSuccessful) {
                        val body: T = response.body() ?: Unit as T
                        callback.onResponse(this@ResultCall, Response.success(Result.success(body)))
                        return
                    }
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(Result.failure(response.toApiError())),
                    )
                }

                override fun onFailure(
                    call: Call<T>,
                    t: Throwable,
                ) {
                    callback.onResponse(this@ResultCall, Response.success(Result.failure(t)))
                }
            },
        )
    }

    override fun isExecuted(): Boolean = call.isExecuted

    override fun cancel() = call.cancel()

    override fun isCanceled(): Boolean = call.isCanceled

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()
}
