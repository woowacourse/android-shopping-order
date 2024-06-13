package woowacourse.shopping.ui.home.action

sealed interface HomeNavigationActions {
    data class NavigateToDetail(val productId: Int) : HomeNavigationActions

    data object NavigateToCart : HomeNavigationActions
}
