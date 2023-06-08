package woowacourse.shopping.util

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.error.WoowaException

fun <T> Call<T>.fetchResponseBody(
    onSuccess: (T) -> Unit,
    onFailure: (WoowaException) -> Unit,
) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    onSuccess(data)
                    return
                }
                onFailure(WoowaException.ResponseBodyNull("Response Body is null / code: ${response.code()}, messsage: ${response.message()}"))
                return
            }
            onFailure(WoowaException.ResponseFailure("Response Failure / code: ${response.code()}, messsage: ${response.message()}"))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(WoowaException.ServerConnectError("throwable: ${t.cause}"))
        }
    })
}

fun <T> Call<T>.fetchHeaderId(
    onSuccess: (Long) -> Unit,
    onFailure: (WoowaException) -> Unit,
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
                onFailure(WoowaException.ResponseBodyNull("code: ${response.code()}, messsage: ${response.message()}"))
                return
            }
            onFailure(WoowaException.ResponseFailure("code: ${response.code()}, messsage: ${response.message()}"))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(WoowaException.ServerConnectError("throwable: ${t.cause}"))
        }
    })
}
