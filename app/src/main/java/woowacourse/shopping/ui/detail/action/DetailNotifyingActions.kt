package woowacourse.shopping.ui.detail.action

sealed interface DetailNotifyingActions {
    data object NotifyPutCartItem : DetailNotifyingActions
}
