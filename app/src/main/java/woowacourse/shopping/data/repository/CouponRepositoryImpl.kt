package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.model.coupon.BogoCouponResponse
import woowacourse.shopping.data.model.coupon.CouponResponse
import woowacourse.shopping.data.model.coupon.DiscountCouponResponse
import woowacourse.shopping.data.model.coupon.FreeShippingCouponResponse
import woowacourse.shopping.data.model.coupon.TimeLimitedCouponResponse
import woowacourse.shopping.data.model.coupon.toDomain
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override fun fetchCoupons(): Result<List<Coupon>> =
        couponRemoteDataSource.fetchCoupons().map { coupons ->
            coupons.map { coupon -> coupon.toTypedDomain() }
        }

    private fun CouponResponse.toTypedDomain(): Coupon =
        when (this) {
            is BogoCouponResponse -> this.toDomain()
            is FreeShippingCouponResponse -> this.toDomain()
            is TimeLimitedCouponResponse -> this.toDomain()
            is DiscountCouponResponse -> this.toDomain()
        }
}
