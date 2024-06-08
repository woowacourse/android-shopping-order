package woowacourse.shopping.presentation.ui.payment

sealed interface PaymentEvent {
    data object FinishOrder : PaymentEvent
}
