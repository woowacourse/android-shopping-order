package woowacourse.shopping.view.detail

import woowacourse.shopping.domain.exception.NetworkError

sealed interface DetailUiEvent {
    data class NavigateToCart(val category: String) : DetailUiEvent

    data class NavigateToLastSeenProduct(val productId: Long) : DetailUiEvent

    data object ShowCannotDecrease : DetailUiEvent

    data class ShowCannotIncrease(val quantity: Int) : DetailUiEvent

    data class ShowErrorMessage(val throwable: NetworkError) : DetailUiEvent
}
