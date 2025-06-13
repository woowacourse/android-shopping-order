package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class DefaultCouponRepository(
    private val dataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun getAll(): List<Coupon> {
        val coupons = dataSource.getAll().getOrThrow()
        return coupons.map {
            when (it.discountType) {
                "buyXgetY" -> it.toBogoCoupon()
                "fixed" -> it.toFixedCoupon()
                "freeShipping" -> it.toFreeShippingCoupon()
                "percentage" -> it.toMiracleSaleCoupon()
                else -> throw IllegalArgumentException("정의되지 않은 쿠폰 타입입니다.")
            }
        }
    }
}
