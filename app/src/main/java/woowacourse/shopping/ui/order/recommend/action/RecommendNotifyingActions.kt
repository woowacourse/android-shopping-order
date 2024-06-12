package woowacourse.shopping.ui.order.recommend.action

sealed interface RecommendNotifyingActions {
    data object NotifyError : RecommendNotifyingActions
}