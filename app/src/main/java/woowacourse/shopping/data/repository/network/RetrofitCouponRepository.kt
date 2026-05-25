package woowacourse.shopping.data.repository.network

import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.service.CouponService
import woowacourse.shopping.data.repository.CouponRepository

class RetrofitCouponRepository(
    private val service: CouponService,
) : CouponRepository {
    override suspend fun getCoupons(): List<Coupon> =
        service
            .getCoupons()
            .map { it.toDomain() }
}
