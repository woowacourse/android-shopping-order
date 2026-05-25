package woowacourse.shopping.ui

sealed interface UiEvent {
    data class ShowSnackbar(
        val message: String,
    ) : UiEvent

    object NavigateToProductList : UiEvent

    object NavigateToCart : UiEvent

    object NavigateToPayment : UiEvent
}
