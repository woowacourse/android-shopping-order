package woowacourse.shopping.utils

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <ResponseType> Call<ResponseType>.enqueueUtil(
    onSuccess: (ResponseType) -> Unit,
    onFailure: ((stateMessage: String) -> Unit)? = null,
) {
    this.enqueue(object : Callback<ResponseType> {
        override fun onResponse(call: Call<ResponseType>, response: Response<ResponseType>) {
            if (response.isSuccessful) {
                onSuccess.invoke(response.body() ?: return)
            } else {
                onFailure?.invoke(response.message())
            }
        }

        override fun onFailure(call: Call<ResponseType>, t: Throwable) {
            Log.d("NetworkTest", "error:$t")
        }
    })
}
