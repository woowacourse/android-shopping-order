package woowacourse.shopping.viewmodel.fakes

import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.CouponRepository
import woowacourse.shopping.domain.coupon.Coupon

class FakeCouponRepository : CouponRepository {
    private var coupons = listOf<Coupon>()

    fun setCoupons(newCoupons: List<Coupon>) {
        coupons = newCoupons
    }

    override suspend fun getCoupons(): ApiResult<List<Coupon>> = ApiResult.Success(coupons)
}
