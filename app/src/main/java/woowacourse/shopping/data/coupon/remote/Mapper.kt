package woowacourse.shopping.data.coupon.remote

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.BuyXgetYCoupon
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.FixedCoupon
import woowacourse.shopping.domain.model.FreeShippingCoupon
import woowacourse.shopping.domain.model.PercentageCoupon
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalTime

private const val INVALID_COUPON_DTO_PARAMETER = "쿠폰의 매개변수가 올바르지 않습니다."

fun List<CouponDto>.toCoupons(): List<Coupon> = map { it.toCoupon() }

fun CouponDto.toCoupon(): Coupon {
    val discountType = DiscountType.from(discountType)
    return when (discountType) {
        DiscountType.FIXED -> this.toFixedCoupon()
        DiscountType.BUY_X_GET_Y -> this.toBuyXgetYCoupon()
        DiscountType.FREE_SHIPPING -> this.toFreeShippingCoupon()
        DiscountType.PERCENTAGE -> this.toPercentageCoupon()
    }
}

private fun CouponDto.toFixedCoupon(): FixedCoupon {
    return FixedCoupon(
        id,
        code,
        description,
        LocalDate.parse(expirationDate) ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
        discount ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
        minimumAmount ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
    )
}

private fun CouponDto.toBuyXgetYCoupon(): BuyXgetYCoupon {
    return BuyXgetYCoupon(
        id,
        code,
        description,
        LocalDate.parse(expirationDate) ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
        buyQuantity ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
        getQuantity ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
    )
}

private fun CouponDto.toFreeShippingCoupon(): FreeShippingCoupon {
    return FreeShippingCoupon(
        id,
        code,
        description,
        LocalDate.parse(expirationDate) ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
        minimumAmount ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
    )
}

private fun CouponDto.toPercentageCoupon(): PercentageCoupon {
    return PercentageCoupon(
        id,
        code,
        description,
        LocalDate.parse(expirationDate) ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
        discount ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
        availableTime?.toAvailableTime() ?: throw IllegalArgumentException(INVALID_COUPON_DTO_PARAMETER),
    )
}

private fun AvailableTimeDto.toAvailableTime(): AvailableTime {
    return AvailableTime(LocalTime.parse(start), LocalTime.parse(end))
}
