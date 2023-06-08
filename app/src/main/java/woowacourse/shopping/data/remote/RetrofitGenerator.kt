package woowacourse.shopping.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.UserAuthorization

object RetrofitGenerator {
    fun <T> create(url: String, service: Class<T>): T {
        val okHttpClient = OkHttpClient.Builder().addInterceptor {
            val original = it.request()
            if (!original.url.encodedPath.equals("/products", true)) {
                it.proceed(
                    original.newBuilder().apply {
                        addHeader("Authorization", "Basic ${UserAuthorization.create("dooly@dooly.com", "1234")}")
                    }.build(),
                )
            } else {
                it.proceed(original)
            }
        }.build()
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
    }
}
