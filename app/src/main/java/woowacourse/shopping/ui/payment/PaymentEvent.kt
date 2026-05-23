package woowacourse.shopping.ui.payment

sealed interface PaymentEvent {
    data class SnackbarEvent(val message: String) : PaymentEvent
    object Order : PaymentEvent
    object NavigateBack : PaymentEvent
    object NavigateToShopping : PaymentEvent
}
