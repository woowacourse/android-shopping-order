package woowacourse.shopping.ui.coupon.uimodel

sealed interface CouponError {
    data object Order : CouponError

    data object LoadCoupon : CouponError
}
