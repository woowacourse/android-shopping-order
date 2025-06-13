package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.coupon.CouponResponse
import woowacourse.shopping.data.service.CouponService

class CouponRemoteDataSourceImpl(
    private val service: CouponService,
) : CouponRemoteDataSource {
    override suspend fun fetchCoupons(): List<CouponResponse> = service.fetchCoupons()
}
