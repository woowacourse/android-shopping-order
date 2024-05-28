package woowacourse.shopping.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    val retrofitService: Retrofit =
        Retrofit.Builder()
            .baseUrl("http://54.180.95.212:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
