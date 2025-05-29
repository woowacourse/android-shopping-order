package woowacourse.shopping.data.remote.product

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProductClient {
    fun getRetrofitService(): ProductService {
        val contentType = "application/json".toMediaType()

        val retrofitService = Retrofit.Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(ProductService::class.java)

        return retrofitService
    }
}
