package woowacourse.shopping.data.remote

import okhttp3.OkHttpClient
import okhttp3.Request

object NetworkModule {
    private const val HONGSIL_SERVER = 0
    private const val MATTHEW_SERVER = 1
    private lateinit var baseUrl: String
    private var client: OkHttpClient = OkHttpClient()

    val request: Request = Request.Builder()
        .url(baseUrl)
        .build()

//    private val retrofit = Retrofit.Builder()
//        .baseUrl(baseUrl)

    fun getServerKey(key: Int) {
        baseUrl = when (key) {
            HONGSIL_SERVER -> "http://3.34.134.115:8080"
            MATTHEW_SERVER -> "http"
            else -> throw IllegalStateException()
        }
    }
}
