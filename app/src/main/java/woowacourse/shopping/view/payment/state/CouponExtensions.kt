package woowacourse.shopping.view.payment.state

import woowacourse.shopping.domain.coupon.BogoCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon

fun Coupon.toCouponUi(): CouponUi {
    return when(this) {
        is BogoCoupon -> CouponUi(
            title = this.description,
            expirationYear = this.expirationDate.year,
            expirationMonth = this.expirationDate.monthValue,
            expirationDay = this.expirationDate.dayOfMonth,
        )
        is FixedCoupon -> CouponUi(
            title = this.description,
            expirationYear = this.expirationDate.year,
            expirationMonth = this.expirationDate.monthValue,
            expirationDay = this.expirationDate.dayOfMonth,
            minimumAmount = this.minimumAmount,
        )
        is FreeShippingCoupon -> CouponUi(
            title = this.description,
            expirationYear = this.expirationDate.year,
            expirationMonth = this.expirationDate.monthValue,
            expirationDay = this.expirationDate.dayOfMonth,
            minimumAmount = this.minimumAmount,
        )
        is MiracleSaleCoupon -> CouponUi(
            title = this.description,
            expirationYear = this.expirationDate.year,
            expirationMonth = this.expirationDate.monthValue,
            expirationDay = this.expirationDate.dayOfMonth,
        )
    }
}
