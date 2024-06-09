package woowacourse.shopping.ui.coupon.uimodel

sealed interface CouponError {
    data object Order : CouponError

    data object LoadCoupon : CouponError

    data object InvalidAuthorized : CouponError

    data object Network : CouponError

    data object UnKnown : CouponError
}
