package woowacourse.shopping.data.source.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.BasicAuthInterceptor
import woowacourse.shopping.data.source.remote.api.CartApiService
import woowacourse.shopping.data.source.remote.api.CouponApiService
import woowacourse.shopping.data.source.remote.api.OrderApiService
import woowacourse.shopping.data.source.remote.api.ProductsApiService

object Client {

    val getProductsApiService: ProductsApiService by lazy {
        buildRetrofit(client = defaultOkHttpClient)
            .create(ProductsApiService::class.java)
    }

    val getCartApiService: CartApiService by lazy {
        buildRetrofit(client = authOkHttpClient)
            .create(CartApiService::class.java)
    }

    val getCouponApiService: CouponApiService by lazy {
        buildRetrofit(client = defaultOkHttpClient, json = couponJson)
            .create(CouponApiService::class.java)
    }

    val getOrderApiService: OrderApiService by lazy {
        buildRetrofit(client = authOkHttpClient)
            .create(OrderApiService::class.java)
    }

    private const val BASE_URL = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com"

    private val contentType = "application/json".toMediaType()

    private val basicJson = Json {
        ignoreUnknownKeys = true
    }

    private val couponJson = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "discountType"
    }

    private val defaultOkHttpClient: OkHttpClient = OkHttpClient.Builder().build()

    private val authOkHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(BasicAuthInterceptor())
        .build()

    private fun buildRetrofit(
        client: OkHttpClient,
        json: Json = basicJson
    ): Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
}
