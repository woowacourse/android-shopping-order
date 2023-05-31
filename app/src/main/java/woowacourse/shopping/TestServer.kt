package woowacourse.shopping

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request

class TestServer {

    fun getProducts() {
        val baseUrl = "http://43.200.181.131:8080/products"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(baseUrl)
            .build()
        otherThread {
            val body = okHttpClient.newCall(request).execute().body?.string()
            Log.d("asdf", "body: $body")
        }
    }

    private fun otherThread(action: () -> Unit) {
        Thread {
            action()
        }.let {
            it.start()
            it.join()
        }
    }
}
