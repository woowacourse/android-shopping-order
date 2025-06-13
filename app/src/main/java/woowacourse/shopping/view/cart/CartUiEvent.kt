package woowacourse.shopping.view.cart

import woowacourse.shopping.domain.cart.ShoppingCart

sealed interface CartUiEvent {
    data class ShowCannotIncrease(val quantity: Int) : CartUiEvent

    data object NavigationToRecommendScreen : CartUiEvent

    data class NavigationToOrderScreen(val orders: List<ShoppingCart>) : CartUiEvent
}
