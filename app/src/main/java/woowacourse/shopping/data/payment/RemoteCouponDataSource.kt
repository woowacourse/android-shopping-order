package woowacourse.shopping.data.payment

import android.util.Log
import woowacourse.shopping.data.API
import woowacourse.shopping.data.payment.CouponDataModel.Companion.toDataModel

class RemoteCouponDataSource(
    private val service: CouponService = API.couponService,
) : CouponDataSource {
    override suspend fun loadCoupons(): List<CouponDataModel> {
        val coupons: List<CouponResponse> = service.getCoupons()
        Log.e("TAG", "coupons: $coupons")
        return coupons.mapNotNull { it.toDataModel() }
    }
}
