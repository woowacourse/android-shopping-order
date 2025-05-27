package woowacourse.shopping.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.cart.api.CartApiService

object RetrofitClient {
    val cartApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CartApiService::class.java)

    val productApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CartApiService::class.java)

    const val BASE_URL = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com"
}