package woowacourse.shopping.data.coupon.remote

import woowacourse.shopping.data.remote.RetrofitClient.couponApi
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

object RemoteCouponRepository : CouponRepository {
    override suspend fun findAll(): Result<List<Coupon>> {
        return couponApi.requestCoupons().map { it.toCoupons() }
    }
}
