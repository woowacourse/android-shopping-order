package woowacourse.shopping.ui.order.action

sealed class OrderNavigationActions {
    data object NavigateToBack: OrderNavigationActions()

    data object NavigateToRecommend: OrderNavigationActions()
}