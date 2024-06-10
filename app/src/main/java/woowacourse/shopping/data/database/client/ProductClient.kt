package woowacourse.shopping.data.database.client

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.model.dto.coupon.CouponDto
import woowacourse.shopping.domain.service.RetrofitService

object ProductClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    private val gsonCouponParser = GsonBuilder()
        .registerTypeAdapter(CouponDto::class.java, CouponDeserializer())
        .create()

    val client: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gsonCouponParser))
            .client(provideOkHttpClient(AppInterceptor()))
            .build()

    val service: RetrofitService = client.create(RetrofitService::class.java)

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }
}
