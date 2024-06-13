package woowacourse.shopping.ui.detail.action

sealed interface DetailNavigationActions {
    data object NavigateToRecentDetail : DetailNavigationActions

    data object NavigateToBack : DetailNavigationActions
}
