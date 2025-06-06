package woowacourse.shopping.view.cart

import woowacourse.shopping.domain.cart.ShoppingCart

sealed interface CartUiEvent {
    data class ShowCannotIncrease(val quantity: Int) : CartUiEvent

    data class ChangeScreen(val orders: List<ShoppingCart>?) : CartUiEvent

    data class ShowErrorMessage(val throwable: Throwable) : CartUiEvent
}
