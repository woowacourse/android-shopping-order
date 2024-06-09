package woowacourse.shopping.view.cart.list

sealed interface CartListUiEvent {
    data class NavigateToProductDetail(
        val productId: Int,
        val lastlyViewed: Boolean,
    ) : CartListUiEvent

    data object NavigateToRecommendList : CartListUiEvent

    data object NavigateBack : CartListUiEvent
}

