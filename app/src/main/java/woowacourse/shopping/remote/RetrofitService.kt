package woowacourse.shopping.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor("hxeyexn", "password"))
            .build()

    val retrofitService: Retrofit =
        Retrofit.Builder()
            .baseUrl("http://54.180.95.212:8080")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
