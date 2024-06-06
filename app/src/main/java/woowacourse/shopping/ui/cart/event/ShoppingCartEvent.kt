package woowacourse.shopping.ui.cart.event

sealed class ShoppingCartEvent {
    data object NavigationOrder : ShoppingCartEvent()

    data object PopBackStack : ShoppingCartEvent()
}
