package woowacourse.shopping.data

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

fun <T> Call<T>.enqueueWithResult(callback: (Result<T?>) -> Unit) {
    enqueue(
        object : Callback<T> {
            override fun onResponse(
                call: Call<T>,
                response: Response<T>,
            ) {
                val result =
                    if (response.isSuccessful) {
                        Result.success(response.body())
                    } else {
                        Result.failure(HttpException(response))
                    }
                callback.invoke(result)
            }

            override fun onFailure(
                call: Call<T>,
                t: Throwable,
            ) {
                callback.invoke(Result.failure(t))
            }
        },
    )
}

inline fun <T, R> Call<T>.enqueueWithTransform(
    crossinline transform: (Response<T>) -> R,
    crossinline callback: (Result<R>) -> Unit,
) {
    enqueue(
        object : Callback<T> {
            override fun onResponse(
                call: Call<T>,
                response: Response<T>,
            ) {
                val transformed = transform(response)
                if (transformed != null) {
                    callback(Result.success(transformed))
                } else {
                    callback(Result.failure(NullPointerException()))
                }
            }

            override fun onFailure(
                call: Call<T>,
                t: Throwable,
            ) {
                callback(Result.failure(t))
            }
        },
    )
}

fun <T> Call<T>.enqueueWithExtractHeaderLocationResult(callback: (Result<String?>) -> Unit) {
    enqueue(
        object : Callback<T> {
            override fun onResponse(
                call: Call<T>,
                response: Response<T>,
            ) {
                if (response.isSuccessful) {
                    val header = response.headers()
                    val location =
                        header["Location"]
                            ?.split("/")
                            ?.last()
                    callback(Result.success(location))
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
        },
    )
}
