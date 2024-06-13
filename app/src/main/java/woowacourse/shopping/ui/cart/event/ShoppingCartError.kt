package woowacourse.shopping.ui.cart.event

sealed interface ShoppingCartError {
    data object DeleteCartItem : ShoppingCartError

    data object UpdateCartItems : ShoppingCartError

    data object LoadCartProducts: ShoppingCartError

    data object EmptyOrderProduct : ShoppingCartError

    data object SaveOrderItems: ShoppingCartError
}
