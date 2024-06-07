package woowacourse.shopping.data.coupon

import woowacourse.shopping.data.dto.response.ResponseCouponDto

abstract class CouponState {
    abstract val coupon: Coupon

    abstract fun condition(): String

    abstract fun isValid(): Boolean

    abstract fun discountAmount(): Int
}

fun ResponseCouponDto.toDomain(): CouponState =
    when (CouponCode.findCode(this.code)) {
        CouponCode.FIXED5000 -> Fixed5000(coupon = this.toCoupon())
        CouponCode.BOGO -> Bogo(coupon = this.toCoupon())
        CouponCode.FREESHIPPING -> Freeshipping(coupon = this.toCoupon())
        CouponCode.MIRACLESALE -> MiracleSale(coupon = this.toCoupon())
    }
