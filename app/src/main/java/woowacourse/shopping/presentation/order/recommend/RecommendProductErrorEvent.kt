package woowacourse.shopping.presentation.order.recommend

sealed interface RecommendProductErrorEvent {
    data object IncreaseCartProduct : RecommendProductErrorEvent

    data object DecreaseCartProduct : RecommendProductErrorEvent

    data object OrderProducts : RecommendProductErrorEvent
}
