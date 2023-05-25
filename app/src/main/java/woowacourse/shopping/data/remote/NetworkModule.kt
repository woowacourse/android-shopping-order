package woowacourse.shopping.data.remote

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

object NetworkModule {

    private const val URL = "http://3.34.134.115:8080"
    private val okHttpClient = OkHttpClient()
    private const val HONGSIL_SERVER = 0
    private const val MATTHEW_SERVER = 1
    private lateinit var baseUrl: String
    private var client: OkHttpClient = OkHttpClient()

    val request: Request = Request.Builder()
        .url(baseUrl)
        .build()

    fun callRequest(path: String): Call {
        val request = Request.Builder().url(URL + path).build()
        return okHttpClient.newCall(request)
    }

    fun getServerKey(key: Int) {
        baseUrl = when (key) {
            HONGSIL_SERVER -> "http://3.34.134.115:8080"
            MATTHEW_SERVER -> "http"
            else -> throw IllegalStateException()
        }
    }
}
