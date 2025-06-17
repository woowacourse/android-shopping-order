package woowacourse.shopping.data.source

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.dto.coupon.Coupon
import woowacourse.shopping.data.service.CouponService
import woowacourse.shopping.data.service.RetrofitProductService

class CouponRemoteDataSource(
    private val retrofitService: CouponService =
        RetrofitProductService.INSTANCE.create(
            CouponService::class.java,
        ),
) : CouponDataSource {
    override suspend fun getCoupons(): List<Coupon> =
        withContext(Dispatchers.IO) {
            val response = retrofitService.getCoupons()

            if (!response.isSuccessful) {
                Log.d("error", "error : $response")
            }

            val coupons: List<Coupon> = response.body() ?: emptyList()

            coupons
        }
}
