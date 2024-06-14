package woowacourse.shopping.presentation.ui.shoppingcart.payment

sealed interface PaymentNavigateAction {
    data object NavigateToProductList : PaymentNavigateAction
}
