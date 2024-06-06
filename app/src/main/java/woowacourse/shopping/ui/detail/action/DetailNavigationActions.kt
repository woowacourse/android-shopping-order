package woowacourse.shopping.ui.detail.action

sealed class DetailNavigationActions {
    data object NavigateToCart: DetailNavigationActions()

    data object NavigateToRecentDetail: DetailNavigationActions()

    data object NavigateToBack: DetailNavigationActions()
}