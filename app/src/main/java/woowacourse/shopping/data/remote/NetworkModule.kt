package woowacourse.shopping.data.remote

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

object NetworkModule {

    private const val BASE_URL = "http://3.34.134.115:8080"
    private val okHttpClient = OkHttpClient()
    private const val HONGSIL_SERVER = 0
    private const val MATTHEW_SERVER = 1
    private lateinit var baseUrl: String

    fun getService(path: String): Call {
        val request = Request.Builder().url(BASE_URL + path).build()
        return okHttpClient.newCall(request)
    }

    fun postService(path: String, requestBody: RequestBody?): Call {
        val request = Request.Builder()
            .url(BASE_URL + path)
            .post(requireNotNull(requestBody))
            .build()

        return okHttpClient.newCall(request)
    }

    fun patchService(path: String, requestBody: RequestBody?): Call {
        val request = Request.Builder()
            .url(BASE_URL + path)
            .post(requireNotNull(requestBody))
            .build()

        return okHttpClient.newCall(request)
    }

    fun deleteService(path: String): Call {
        val request = Request.Builder().url(BASE_URL + path).build()
        return okHttpClient.newCall(request)
    }

//    fun getServerKey(key: Int) {
//        baseUrl = when (key) {
//            HONGSIL_SERVER -> "http://3.34.134.115:8080"
//            MATTHEW_SERVER -> "http"
//            else -> throw IllegalStateException()
//        }
//    }
//
//        val request: Request = Request.Builder()
//        .url(baseUrl)
//        .build()
}
