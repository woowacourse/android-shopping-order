package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.CouponApi
import woowacourse.shopping.data.model.response.CouponResponse.Companion.toDomain
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponDetail.Companion.toCoupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepository(
    private val api: CouponApi,
) : CouponRepository {
    override suspend fun fetchAllCoupons(): List<Coupon> =
        api
            .getCoupons()
            .mapNotNull { it.toDomain()?.toCoupon() }
}
