package woowacourse.shopping.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import woowacourse.shopping.data.service.ProductApiService

object RetrofitInstance {
    private val contentType = "application/json".toMediaType()
    val retrofitService: ProductApiService =
        Retrofit.Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(ProductApiService::class.java)
}
