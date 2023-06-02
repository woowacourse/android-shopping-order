package woowacourse.shopping.server.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    lateinit var baseUrl: String

    val orderPayDTO: PayService by lazy {
        instance.create(PayService::class.java)
    }

    private fun getRetrofit() = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val instance: Retrofit
        get() = getRetrofit()

    private fun initBaseUrl(url: String) {
        baseUrl = url
    }
}
