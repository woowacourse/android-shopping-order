package woowacourse.shopping.presentation.view.util

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val BASIC_USER_TOKEN = "Basic %s"
        private const val CONTENT_TYPE = "Content-Type"
        private const val JSON = "application/json"
    }
}
