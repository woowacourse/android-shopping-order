package woowacourse.shopping.ui.payment

sealed interface PaymentEvent {
    data class ShowSnackbar(val message: String): PaymentEvent
    data object PaySuccess: PaymentEvent
}
