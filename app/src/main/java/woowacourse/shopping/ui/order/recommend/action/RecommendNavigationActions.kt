package woowacourse.shopping.ui.order.recommend.action

sealed interface RecommendNavigationActions {
    data class NavigateToDetail(val productId: Int) : RecommendNavigationActions
}