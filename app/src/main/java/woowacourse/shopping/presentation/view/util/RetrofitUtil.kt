package woowacourse.shopping.presentation.view.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.model.Server

fun createRetrofit(server: Server): Retrofit {
    return Retrofit
        .Builder()
        .baseUrl(server.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
