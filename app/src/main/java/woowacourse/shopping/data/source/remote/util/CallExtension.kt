package woowacourse.shopping.data.source.remote.util

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val RESPONSE_ERROR = "[ERROR] 서버가 응답하지 않았습니다."

fun <T> Call<T>.enqueueResult(
    onResult: (Result<T>) -> Unit,
) {
    this.enqueue(object : Callback<T> {
        override fun onResponse(
            call: Call<T>,
            response: Response<T>,
        ) {
            val body = response.body()
            if (response.isSuccessful && body != null) {
                onResult(Result.success(body))
            } else {
                onResult(Result.failure(Exception(RESPONSE_ERROR)))
            }
        }

        override fun onFailure(
            call: Call<T>,
            t: Throwable,
        ) {
            onResult(Result.failure(t))
        }
    })
}