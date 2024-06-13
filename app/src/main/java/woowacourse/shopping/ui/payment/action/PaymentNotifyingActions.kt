package woowacourse.shopping.ui.payment.action

sealed interface PaymentNotifyingActions {
    data object NotifyPaymentCompleted : PaymentNotifyingActions
}
