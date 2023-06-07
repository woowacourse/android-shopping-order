package woowacourse.shopping.data.util

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> responseCallback(
    onFailure: (Throwable) -> Unit,
    onSuccess: (T) -> Unit,
): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful.not()) {
                onFailure(Throwable())
                return
            }

            response.body()?.let {
                onSuccess(it)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.e("Request Failed", t.toString())
            onFailure(t)
        }
    }
}
