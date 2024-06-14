package woowacourse.shopping.view.order

sealed interface OrderUiEvent {
    data object NavigateBackToHome : OrderUiEvent

    data object Error : OrderUiEvent
}
