package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.coupon.dto.CouponResponseItem
import woowacourse.shopping.domain.coupon.BoGoCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon
import java.time.LocalDate
import java.time.LocalTime

fun CouponResponseItem.FixedDiscountCoupon.toDomain(): Coupon? {
    if (this.id == null || this.description == null || this.discount == null || this.minimumAmount == null) {
        return null
    }
    return FixedCoupon(
        couponId = this.id,
        description = this.description,
        expirationDate = LocalDate.parse(this.expirationDate),
        disCountPrice = this.discount,
        minimumOrderPrice = this.minimumAmount
    )
}

fun CouponResponseItem.PercentageCoupon.toDomain(): Coupon? {
    if (this.id == null || this.description == null || this.availableTime == null || this.discount == null) {
        return null
    }
    return MiracleSaleCoupon(
        couponId = this.id,
        description = this.description,
        expirationDate = LocalDate.parse(this.expirationDate),
        startHour = LocalTime.parse(this.availableTime.start),
        endHour = LocalTime.parse(this.availableTime.end),
        discountRate = this.discount.toDouble()
    )
}

fun CouponResponseItem.BuyXGetYCoupon.toDomain(): Coupon? {
    if (this.id == null || this.description == null || this.buyQuantity == null || this.getQuantity == null) {
        return null
    }
    return BoGoCoupon(
        couponId = this.id,
        description = this.description,
        expirationDate = LocalDate.parse(this.expirationDate),
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity
    )
}

fun CouponResponseItem.FreeShippingCoupon.toDomain(): Coupon? {
    if (this.id == null || this.description == null || this.minimumAmount == null) {
        return null
    }
    return FreeShippingCoupon(
        couponId = this.id,
        description = this.description,
        expirationDate = LocalDate.parse(this.expirationDate),
        minimumOrderPrice = this.minimumAmount
    )
}