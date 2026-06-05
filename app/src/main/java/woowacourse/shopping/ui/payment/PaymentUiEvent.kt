package woowacourse.shopping.ui.payment

sealed class PaymentUiEvent {
    object NavToBack : PaymentUiEvent()

    object PaymentSuccess : PaymentUiEvent()

    data class ShowToastMessage(
        val message: String,
    ) : PaymentUiEvent()
}
