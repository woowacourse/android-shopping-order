package woowacourse.shopping.domain.model.coupon

import java.time.LocalTime

data class CouponPercentDiscount(
    override val couponBase: CouponBase,
    val discountPercent: Int,
    val availableStartTime: LocalTime,
    val availableEndTime: LocalTime,
    val discountType: CouponDiscountType,
) : Coupon()
