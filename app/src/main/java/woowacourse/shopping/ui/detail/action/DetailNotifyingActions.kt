package woowacourse.shopping.ui.detail.action

sealed interface DetailNotifyingActions {
    data object NotifyPutInCartItem : DetailNotifyingActions

    data object NotifyError : DetailNotifyingActions
}
