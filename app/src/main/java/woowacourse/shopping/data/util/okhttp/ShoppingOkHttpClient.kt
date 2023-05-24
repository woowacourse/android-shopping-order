package woowacourse.shopping.data.util.okhttp

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.util.okhttp.Header.AUTHORIZATION
import java.io.IOException
import java.util.concurrent.CountDownLatch

object ShoppingOkHttpClient : OkHttpClient() {
    private val TOKEN: String?
        get() = ShoppingApplication.pref.getToken()

    fun enqueue(
        request: Request,
        onSuccess: (call: Call, response: Response) -> Unit,
        onFailed: (call: Call, exception: IOException) -> Unit,
    ): CountDownLatch {
        val countDownLatch = CountDownLatch(1)

        newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onSuccess(call, response)
                } else {
                    onFailed(call, IOException())
                }
                countDownLatch.countDown()
            }

            override fun onFailure(call: Call, e: IOException) {
                onFailed(call, e)
                countDownLatch.countDown()
            }
        })

        return countDownLatch
    }

    fun get(url: String): Request {
        return buildRequest(url) { get() }
    }

    fun post(url: String, requestBody: RequestBody): Request {
        return buildRequest(url) { post(requestBody) }
    }

    fun put(url: String, requestBody: RequestBody): Request {
        return buildRequest(url) { put(requestBody) }
    }

    fun delete(url: String, requestBody: RequestBody? = null): Request {
        return buildRequest(url) { delete(requestBody) }
    }

    fun patch(url: String, requestBody: RequestBody): Request {
        return buildRequest(url) { patch(requestBody) }
    }

    private fun buildRequest(url: String, builder: Request.Builder.() -> Request.Builder): Request {
        return Request.Builder()
            .url(url)
            .header(Header.of(AUTHORIZATION), "Basic $TOKEN")
            .builder()
            .build()
    }
}
