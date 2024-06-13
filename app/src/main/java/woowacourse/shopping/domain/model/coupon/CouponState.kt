package woowacourse.shopping.domain.model.coupon

abstract class CouponState {
    abstract val coupon: Coupon

    abstract fun condition(): String

    abstract fun isValid(): Boolean

    abstract fun discountAmount(): Int
}
