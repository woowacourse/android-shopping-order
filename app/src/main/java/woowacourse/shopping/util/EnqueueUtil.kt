package woowacourse.shopping.util

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.domain.util.Error

private val TAG = "ENQUEUE_UTIL"

fun <T> Call<T>.fetchResponseBody(
    onSuccess: (T) -> Unit,
    onFailure: (Error) -> Unit,
) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    onSuccess(data)
                    return
                }
                onFailure(Error.ResponseBodyNull)
                Log.e(
                    TAG,
                    "Response Body is null / code: ${response.code()}, messsage: ${response.message()}",
                )
                return
            }
            onFailure(Error.ResponseFailure)
            Log.e(
                TAG,
                "Response Failure / code: ${response.code()}, messsage: ${response.message()}",
            )
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(Error.ServerConnectError)
            Log.e(TAG, "throwable: ${t.cause}")
        }
    })
}

fun <T> Call<T>.fetchHeaderId(
    onSuccess: (Long) -> Unit,
    onFailure: (Error) -> Unit,
) {
    enqueue(object :
        Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                val id: Long? = response.headers()["Location"]
                    ?.substringAfterLast("/")
                    ?.toLong()
                if (id != null) {
                    onSuccess(id)
                    return
                }
                onFailure(Error.ResponseBodyNull)
                Log.e(TAG, "code: ${response.code()}, messsage: ${response.message()}")
                return
            }
            onFailure(Error.ResponseFailure)
            Log.e(TAG, "code: ${response.code()}, messsage: ${response.message()}")
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(Error.ServerConnectError)
            Log.e(TAG, "throwable: ${t.cause}")
        }
    })
}
