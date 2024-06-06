package woowacourse.shopping.ui.home.action

sealed class HomeNavigationActions {
    data class NavigateToDetail(val productId: Int) : HomeNavigationActions()

    data object NavigateToCart : HomeNavigationActions()
}
