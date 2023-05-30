package woowacourse.shopping.data.service.cart

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.user.ServerInfo

object CartRetrofitApiGenerator {
    private val retrofit: Retrofit =
        Retrofit
            .Builder()
            .baseUrl(ServerInfo.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val service: CartRetrofitApi =
        retrofit.create(CartRetrofitApi::class.java)
}
