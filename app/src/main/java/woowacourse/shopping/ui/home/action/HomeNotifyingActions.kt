package woowacourse.shopping.ui.home.action

sealed interface HomeNotifyingActions {
    data object NotifyError : HomeNotifyingActions
}
