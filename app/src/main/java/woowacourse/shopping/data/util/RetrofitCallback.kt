package woowacourse.shopping.data.util

import android.util.Log
import retrofit2.Call
import retrofit2.Response

abstract class RetrofitCallback<T> : retrofit2.Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        val body = response.body()
        if (response.isSuccessful) {
            onSuccess(body)
        } else {
            onFailure(call, Error("Response unsuccessful"))
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e("Request Failed", t.toString())
    }

    abstract fun onSuccess(response: T?)
}
