package woowacourse.shopping.presentation.ui.payment

interface CouponEvent {
    data object ApplyCoupon : CouponEvent
    data object SuccessPay : CouponEvent
}
