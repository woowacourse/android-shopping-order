package woowacourse.shopping.ui

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data object NavigateToProductList : UiEvent
    data object NavigateToCart : UiEvent
    data object NavigateToPayment : UiEvent
}
