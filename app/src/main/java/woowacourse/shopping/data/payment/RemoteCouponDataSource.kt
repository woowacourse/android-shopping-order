package woowacourse.shopping.data.payment

import woowacourse.shopping.data.API
import woowacourse.shopping.data.payment.CouponDataModel.Companion.toDataModel

class RemoteCouponDataSource(
    private val service: CouponService = API.couponService,
) : CouponDataSource {
    override suspend fun loadCoupons(): List<CouponDataModel> {
        val coupons: CouponResponse = service.getCoupons()
        return coupons.mapNotNull { it.toDataModel() }
    }
}
