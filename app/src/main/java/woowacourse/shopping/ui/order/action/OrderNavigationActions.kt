package woowacourse.shopping.ui.order.action

sealed interface OrderNavigationActions {
    data object NavigateToBack : OrderNavigationActions

    data object NavigateToRecommend : OrderNavigationActions

    data object NavigateToPayment : OrderNavigationActions
}
