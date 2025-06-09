package woowacourse.shopping.data.coupons.repository

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.coupons.CouponRequest
import woowacourse.shopping.data.util.AppInterceptor
import woowacourse.shopping.data.util.RetrofitService

class OrderRemoteDataSourceImpl(
    baseUrl: String = BuildConfig.BASE_URL,
) : OrderRemoteDataSource {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AppInterceptor())
        .build()

    private val retrofitService: RetrofitService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RetrofitService::class.java)


    override suspend fun fetchCoupons(): CouponRequest {
        return retrofitService.requestCoupons()
    }

    override suspend fun postOrder(cartItemIds: List<Int>) {
        retrofitService.addOrder(cartItemIds)
    }
}