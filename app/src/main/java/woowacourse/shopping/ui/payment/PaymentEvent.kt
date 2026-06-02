package woowacourse.shopping.ui.payment

sealed interface PaymentEvent {
    data object Completed : PaymentEvent
}
