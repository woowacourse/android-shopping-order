package woowacourse.shopping.presentation.cart.order

sealed interface OrderErrorEvent {
    data object IncreaseCartProduct : OrderErrorEvent

    data object DecreaseCartProduct : OrderErrorEvent

    data object DeleteCartProduct : OrderErrorEvent

    data object OrderProducts : OrderErrorEvent
}
