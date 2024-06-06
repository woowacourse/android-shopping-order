package woowacourse.shopping.ui.order.recommend.action

sealed class RecommendNavigationActions {
    data class NavigateToDetail(val productId: Int) : RecommendNavigationActions()
}