package woowacourse.shopping.ui.payment

sealed interface PaymentEvent {
    data class SnackbarEvent(val message: String) : PaymentEvent
    data object Order : PaymentEvent
    data object NavigateBack : PaymentEvent
}
