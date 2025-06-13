package woowacourse.shopping.view.receipt

import woowacourse.shopping.domain.coupon.BoGoCoupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon

fun FixedCoupon.toItem(): CouponItem {
    return CouponItem(
        couponId = this.couponId,
        description = this.description,
        expirationDate = this.expirationDate.toString(),
        minimumOrderPrice = this.minimumOrderPrice
    )
}

fun FreeShippingCoupon.toItem(): CouponItem {
    return CouponItem(
        couponId = this.couponId,
        description = this.description,
        expirationDate = this.expirationDate.toString(),
        minimumOrderPrice = this.minimumOrderPrice
    )
}

fun BoGoCoupon.toItem(): CouponItem {
    return CouponItem(
        couponId = this.couponId,
        description = this.description,
        expirationDate = this.expirationDate.toString(),
    )
}

fun MiracleSaleCoupon.toItem(): CouponItem {
    return CouponItem(
        couponId = this.couponId,
        description = this.description,
        expirationDate = this.expirationDate.toString(),
        availableTime = "${this.startHour}~${this.endHour}"
    )
}