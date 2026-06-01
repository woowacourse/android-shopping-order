package woowacourse.shopping.ui.cart

sealed interface CartUiEvent {
    data class ShowSnackbar(val message: String) : CartUiEvent
    data class OrderRequested(val selectedItemIds: List<Int>) : CartUiEvent
}
