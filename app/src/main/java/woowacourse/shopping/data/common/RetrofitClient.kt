package woowacourse.shopping.data.common

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.cart.remote.CartItemApiService
import woowacourse.shopping.data.payment.remote.CouponApiService
import woowacourse.shopping.data.order.remote.OrderApiService
import woowacourse.shopping.data.product.remote.ProductsApiService
import java.lang.reflect.Type

object RetrofitClient {
    val productsApi: ProductsApiService by lazy {
        buildRetrofitService(BASE_URL).create(
            ProductsApiService::class.java,
        )
    }

    val cartItemApi: CartItemApiService by lazy {
        buildRetrofitService(BASE_URL).create(
            CartItemApiService::class.java,
        )
    }

    val orderApi: OrderApiService by lazy {
        buildRetrofitService(BASE_URL).create(
            OrderApiService::class.java,
        )
    }

    val couponApi: CouponApiService by lazy {
        buildRetrofitService(BASE_URL).create(
            CouponApiService::class.java,
        )
    }

    private val logging =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(BuildConfig.USERNAME, BuildConfig.PASSWORD))
            .addInterceptor(logging)
            .build()

    private val nullOnEmptyConverterFactory =
        object : Converter.Factory() {
            fun converterFactory() = this

            override fun responseBodyConverter(
                type: Type,
                annotations: Array<out Annotation>,
                retrofit: Retrofit,
            ) = object : Converter<ResponseBody, Any?> {
                val nextResponseBodyConverter =
                    retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

                override fun convert(value: ResponseBody) =
                    if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
            }
        }

    private const val BASE_URL = "http://54.180.95.212:8080"

    private fun buildRetrofitService(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
