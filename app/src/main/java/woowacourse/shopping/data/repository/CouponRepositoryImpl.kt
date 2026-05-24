package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.api.CouponApi
import woowacourse.shopping.data.remote.mapper.toDomain
import woowacourse.shopping.model.coupon.Coupon

class CouponRepositoryImpl(
    private val api: CouponApi,
) : CouponRepository {
    override suspend fun getCoupons(): List<Coupon> = api.getCoupons().map { it.toDomain() }
}
