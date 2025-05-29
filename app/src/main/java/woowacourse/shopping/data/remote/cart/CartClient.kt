package woowacourse.shopping.data.remote.cart

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object CartClient {
    fun getRetrofitService(): CartService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor("rosemin928", "password"))
            .build()

        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com")
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(CartService::class.java)
    }
}
