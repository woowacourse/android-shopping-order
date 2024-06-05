package woowacourse.shopping.view.payment

sealed interface PaymentEvent{
    sealed interface SelectCoupon : PaymentEvent{
        data object Success : SelectCoupon
        data object InvalidDate : SelectCoupon
        data object InvalidPrice : SelectCoupon
        data object InvalidCount : SelectCoupon
    }
    sealed interface Order : PaymentEvent {
        data object Success : Order
    }
}
