package woowacourse.shopping.ui.order.cart.action

sealed interface CartNavigationActions {
    data class NavigateToDetail(val productId: Int) : CartNavigationActions
}
