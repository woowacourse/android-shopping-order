package woowacourse.shopping.view.cart.recommend

import woowacourse.shopping.domain.model.CartItemDomain

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
