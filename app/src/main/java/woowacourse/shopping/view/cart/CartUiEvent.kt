package woowacourse.shopping.view.cart

import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.exception.NetworkError

sealed interface CartUiEvent {
    data class ShowCannotIncrease(val quantity: Int) : CartUiEvent

    data object ShowNotHasPurchaseCart : CartUiEvent

    data class ChangeScreen(val orders: List<ShoppingCart>?) : CartUiEvent

    data class ShowErrorMessage(val throwable: NetworkError) : CartUiEvent
}
