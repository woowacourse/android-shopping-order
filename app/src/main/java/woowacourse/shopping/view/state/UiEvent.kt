package woowacourse.shopping.view.state

import woowacourse.shopping.domain.model.CartItemDomain

sealed interface HomeUiEvent {
    data class NavigateToDetail(val productId: Int) : HomeUiEvent

    data object NavigateToCart : HomeUiEvent

    data object Error : HomeUiEvent
}

sealed interface DetailUiEvent {
    data class NavigateToRecentProduct(val productId: Int) : DetailUiEvent

    data object ProductAddedToCart : DetailUiEvent

    data object NavigateBack : DetailUiEvent

    data object Error : DetailUiEvent
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

    data object NavigateBackToHome : RecommendListUiEvent

    data class NavigateToOrder(
        val cartItems: List<CartItemDomain>
    ) : RecommendListUiEvent
}

sealed interface OrderUiEvent {
    data object NavigateBackToHome : OrderUiEvent

    data object Error : OrderUiEvent
}
