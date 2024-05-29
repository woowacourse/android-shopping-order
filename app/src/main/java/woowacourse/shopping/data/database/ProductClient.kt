package woowacourse.shopping.data.database

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.domain.service.RetrofitService

object ProductClient {
    val client: Retrofit =
        Retrofit.Builder()
            .baseUrl("http://54.180.95.212:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val service = client.create(RetrofitService::class.java)
}
