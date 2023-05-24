package woowacourse.shopping.data

import okhttp3.OkHttpClient
import okhttp3.Request

object ApiClient {
    var baseUrl = ""
    private val client = OkHttpClient()

    fun getApiService(path: String): String? {
        var responseBody: String? = null
        val thread = Thread {
            val request = Request.Builder().url(baseUrl + path).build()
            val response = client.newCall(request).execute()
            responseBody = response?.body?.string()
        }
        thread.start()
        thread.join()
        return responseBody
    }
}
