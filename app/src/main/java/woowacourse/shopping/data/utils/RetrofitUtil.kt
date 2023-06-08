package woowacourse.shopping.data.utils

import retrofit2.Call
import retrofit2.Callback

fun <T> createResponseCallback(
    onSuccess: (T) -> Unit,
    onFailure: (Exception) -> Unit,
): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
            val responseBody = response.body()
            if (responseBody != null) {
                onSuccess(responseBody)
            } else {
                onFailure(Exception(response.message()))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(Exception(t.message))
        }
    }
}
