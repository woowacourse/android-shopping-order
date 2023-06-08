package woowacourse.shopping.support.framework.data.httpclient

import android.util.Log
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

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

inline fun <reified T> Retrofit.getParsedErrorBody(errorBody: ResponseBody?): T? =
    errorBody?.let {
        responseBodyConverter<T>(
            T::class.java,
            T::class.java.annotations
        ).convert(errorBody)
    }

fun Headers.getIdFromHeaders(headerName: String): Int? =
    this[headerName]?.split("/")?.lastOrNull()?.toInt()
