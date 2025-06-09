package woowacourse.shopping.view.cart

import woowacourse.shopping.domain.cart.ShoppingCarts

sealed interface CartUiEvent {
    data class ShowCannotIncrease(val quantity: Int) : CartUiEvent

    data object ShowNotHasPurchaseCart : CartUiEvent

    data class ChangeScreen(val orders: ShoppingCarts?) : CartUiEvent

    data class ShowErrorMessage(val throwable: Throwable) : CartUiEvent
}
