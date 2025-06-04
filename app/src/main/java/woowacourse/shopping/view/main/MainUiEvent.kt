package woowacourse.shopping.view.main

import woowacourse.shopping.domain.exception.NetworkError

sealed interface MainUiEvent {
    data class ShowCannotIncrease(val quantity: Int) : MainUiEvent

    data class NavigateToDetail(
        val productId: Long,
        val lastSeenProductId: Long? = null,
    ) : MainUiEvent

    data class NavigateToCart(
        val lastSeenProductCategory: String?,
    ) : MainUiEvent

    data class ShowErrorMessage(val throwable: NetworkError) : MainUiEvent
}
