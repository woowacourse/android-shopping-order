package woowacourse.shopping.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig

object RetrofitClient {
    private val logging =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val client =
        OkHttpClient.Builder()
            .addInterceptor(
                BasicAuthInterceptor(
                    username = BuildConfig.USERNAME,
                    password = BuildConfig.PASSWORD,
                ),
            )
            .addInterceptor(logging)
            .build()

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}
