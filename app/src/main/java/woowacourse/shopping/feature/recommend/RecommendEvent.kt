package woowacourse.shopping.feature.recommend

sealed interface RecommendEvent {
    data class NavigateToPurchase(
        val contentIds: List<String>,
        val totalPrice: Int,
    ) : RecommendEvent
}
