package woowacourse.shopping.server.retrofit

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> createResponseCallback(
    onSuccess: (T) -> Unit,
    onFailure: (Exception) -> Unit,
): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val responseBody = response.body()
            if (responseBody != null) {
                onSuccess(responseBody)
            } else {
                onFailure(Exception("Response unsuccessful"))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            println("Response unsuccessful: ${t.message}")
            onFailure(Exception("Response unsuccessful"))
        }
    }
}
