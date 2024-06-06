package woowacourse.shopping.ui.detail.action

sealed class DetailNavigationActions {
    data object NavigateToRecentDetail : DetailNavigationActions()

    data object NavigateToBack : DetailNavigationActions()
}
