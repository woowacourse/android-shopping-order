package woowacourse.shopping.data.util

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> responseBodyCallback(
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

fun responseHeaderLocationCallback(
    onFailure: (Throwable) -> Unit,
    onSuccess: (Long) -> Unit,
): Callback<Unit> {
    return object : Callback<Unit> {
        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
            if (response.isSuccessful.not()) {
                onFailure(Throwable())
                return
            }

            response.headers()["Location"]?.let {
                it.substringAfterLast("/").toLongOrNull()?.let { id ->
                    onSuccess(id)
                }
            }
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
            Log.e("Request Failed", t.toString())
            onFailure(t)
        }
    }
}
