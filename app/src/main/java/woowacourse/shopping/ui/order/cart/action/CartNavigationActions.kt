package woowacourse.shopping.ui.order.cart.action

sealed class CartNavigationActions {
    data class NavigateToDetail(val productId: Int) : CartNavigationActions()
}
