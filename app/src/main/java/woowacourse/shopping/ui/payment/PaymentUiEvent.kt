package woowacourse.shopping.ui.payment

sealed interface PaymentUiEvent {
    data class ShowMessage(
        val message: String,
    ) : PaymentUiEvent

    data object OrderSucceeded : PaymentUiEvent
}
