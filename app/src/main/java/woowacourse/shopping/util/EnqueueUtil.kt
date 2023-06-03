package woowacourse.shopping.util

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.domain.util.Error

fun <T> Call<T>.enqueueUtil(
    onSuccess: (T) -> Unit,
    onFailure: (Error) -> Unit,
) {
    val TAG = "ENQUEUE_UTIL"
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    onSuccess(data)
                    return
                }
                onFailure(Error.ResponseBodyNull)
                Log.e(TAG, "Response Body is null / code: ${response.code()}, messsage: ${response.message()}")
                return
            }
            onFailure(Error.ResponseFailure)
            Log.e(TAG, "Response Failure / code: ${response.code()}, messsage: ${response.message()}")
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(Error.ServerConnectError)
            Log.e(TAG, "throwable: ${t.cause}, messsage: ${t.message}")
        }
    })
}
