package woowacourse.shopping.data.database

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.domain.service.RetrofitService
import java.util.Properties

object ProductClient {
    private val baseUrl = Properties().getProperty("base_url")

    val client: Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(AppInterceptor()))
            .build()

    val service: RetrofitService = client.create(RetrofitService::class.java)

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }
}
