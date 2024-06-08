package woowacourse.shopping.ui.payment.action

sealed interface PaymentNavigationActions {
    data object NavigateToBack : PaymentNavigationActions

    data object NavigateToHome : PaymentNavigationActions
}
