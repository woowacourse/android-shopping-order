package woowacourse.shopping.view.detail

sealed interface DetailUiEvent {
    data class NavigateToCart(val category: String) : DetailUiEvent

    data class NavigateToLastSeenProduct(val productId: Long) : DetailUiEvent

    data object ShowCannotDecrease : DetailUiEvent

    data class ShowCannotIncrease(val quantity: Int) : DetailUiEvent

    object ShowNetworkErrorMessage : DetailUiEvent
}
