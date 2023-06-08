package woowacourse.shopping.presentation.view.util

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.data.model.Server

class RetrofitUtil(
    private val server: Server,
) {

    private fun createInterceptor(): Interceptor = Interceptor { chain ->
        with(chain) {
            val newRequest = request().newBuilder()
                .addHeader(AUTHORIZATION, BASIC_USER_TOKEN.format(Server.TOKEN))
                .addHeader(CONTENT_TYPE, JSON)
                .build()

            proceed(newRequest)
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(createInterceptor())
        }.build()
    }

    fun createRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(server.url)
            .client(createOkHttpClient())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val BASIC_USER_TOKEN = "Basic %s"
        private const val CONTENT_TYPE = "Content-Type"
        private const val JSON = "application/json"
    }
}
