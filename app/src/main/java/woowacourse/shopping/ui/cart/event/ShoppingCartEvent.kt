package woowacourse.shopping.ui.cart.event

sealed interface ShoppingCartEvent {
    data object NavigationOrder : ShoppingCartEvent

    data object PopBackStack : ShoppingCartEvent

    data object DeleteCartItem : ShoppingCartEvent
}
