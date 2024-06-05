package woowacourse.shopping.view.payment

sealed interface PaymentEvent{
    sealed interface SelectCoupon : PaymentEvent{
        data object Success : SelectCoupon
    }
    sealed interface Order : PaymentEvent {
        data object Success : Order
    }
}
