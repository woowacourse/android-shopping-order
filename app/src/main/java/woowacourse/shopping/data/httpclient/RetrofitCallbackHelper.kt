package woowacourse.shopping.data.httpclient

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> getRetrofitCallback(
    failureLogTag: String = "no tag",
    onFailure: ((call: Call<T>, t: Throwable) -> Unit) = { _, _ -> },
    onResponse: ((call: Call<T>, response: Response<T>) -> Unit) = { _, _ -> }
): Callback<T> = object : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        onResponse(call, response)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e(failureLogTag, t.message ?: "Exception에 message가 없습니다.")
        onFailure(call, t)
    }
}
