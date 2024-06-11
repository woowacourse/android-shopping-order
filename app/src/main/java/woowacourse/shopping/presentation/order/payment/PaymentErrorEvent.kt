package woowacourse.shopping.presentation.order.payment

sealed interface PaymentErrorEvent {
    data object OrderProducts : PaymentErrorEvent

    data object LoadCoupons : PaymentErrorEvent
}
