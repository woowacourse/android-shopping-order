package woowacourse.shopping.view.state

sealed interface HomeUiEvent {
    data class NavigateToDetail(val productId: Int) : HomeUiEvent

    data object NavigateToCart : HomeUiEvent
}

sealed interface DetailUiEvent {
    data class NavigateToRecentProduct(val productId: Int) : DetailUiEvent

    data object NavigateToCart : DetailUiEvent

    data object NavigateBack : DetailUiEvent
}

sealed interface CartListUiEvent {
    data class NavigateToProductDetail(
        val productId: Int,
        val lastlyViewed: Boolean,
    ) : CartListUiEvent

    data object NavigateToRecommendList : CartListUiEvent

    data object NavigateBack : CartListUiEvent
}

sealed interface RecommendListUiEvent {
    data object NavigateBackToCartList : RecommendListUiEvent

    data class NavigateToProductDetail(
        val productId: Int,
        val lastlyViewed: Boolean,
    ) : RecommendListUiEvent
}
