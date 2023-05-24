package woowacourse.shopping.data.remote

import retrofit2.Retrofit

object NetworkModule {
    private const val HONGSIL_SERVER = 0
    private const val MATTHEW_SERVER = 1
    private lateinit var baseUrl: String

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)

    fun getServerKey(key: Int) {
        baseUrl = when (key) {
            HONGSIL_SERVER -> "http://3.34.134.115:8080"
            MATTHEW_SERVER -> "http"
            else -> throw IllegalStateException()
        }
    }
}
