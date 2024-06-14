package woowacourse.shopping.view.detail

sealed interface DetailUiEvent {
    data class NavigateToRecentProduct(val productId: Int) : DetailUiEvent

    data object ProductAddedToCart : DetailUiEvent

    data object NavigateBack : DetailUiEvent

    data object Error : DetailUiEvent
}
