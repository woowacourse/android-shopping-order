package woowacourse.shopping.data.coupons.repository

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.coupons.CouponRequest
import woowacourse.shopping.data.coupons.OrderRequest
import woowacourse.shopping.data.util.AppInterceptor
import woowacourse.shopping.data.util.RetrofitService
import woowacourse.shopping.domain.model.Authorization

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
        retrofitService.addOrder(OrderRequest(cartItemIds),"Basic ${Authorization.basicKey}")
    }
}