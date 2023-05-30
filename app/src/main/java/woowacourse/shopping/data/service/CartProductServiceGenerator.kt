package woowacourse.shopping.data.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.user.ServerInfo

object CartProductServiceGenerator {
    private val retrofit: Retrofit =
        Retrofit
            .Builder()
            .baseUrl(ServerInfo.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val service: CartProductRetrofitApi =
        retrofit.create(CartProductRetrofitApi::class.java)
}
