package woowacourse.shopping.presentation.ui.payment

sealed interface PaymentNavigateAction {
    data object NavigateToProductList : PaymentNavigateAction
}
