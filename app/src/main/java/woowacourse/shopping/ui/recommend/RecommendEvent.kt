package woowacourse.shopping.ui.recommend

sealed interface RecommendEvent {
    data object UpdateCartItemFailure : RecommendEvent
}
