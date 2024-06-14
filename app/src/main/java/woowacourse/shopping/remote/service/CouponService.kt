package woowacourse.shopping.remote.service

import retrofit2.Response
import retrofit2.create
import retrofit2.http.GET
import woowacourse.shopping.remote.RetrofitModule
import woowacourse.shopping.remote.dto.response.CouponResponse

interface CouponService {
    @GET("coupons")
    suspend fun loadCoupons(): Response<List<CouponResponse>>

    companion object {
        private var instance: CouponService? = null

        fun instance(): CouponService =
            instance ?: synchronized(this) {
                instance ?: RetrofitModule.retrofit().create<CouponService>()
                    .also { instance = it }
            }
    }
}
