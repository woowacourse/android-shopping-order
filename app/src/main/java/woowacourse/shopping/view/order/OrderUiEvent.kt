package woowacourse.shopping.view.order

sealed interface OrderUiEvent {
    data class ShowErrorMessage(val throwable: Throwable) : OrderUiEvent
}
