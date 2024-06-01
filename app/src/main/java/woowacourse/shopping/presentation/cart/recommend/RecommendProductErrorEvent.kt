package woowacourse.shopping.presentation.cart.recommend

sealed interface RecommendProductErrorEvent {
    data object IncreaseCartProduct : RecommendProductErrorEvent

    data object DecreaseCartProduct : RecommendProductErrorEvent

    data object DeleteCartProduct : RecommendProductErrorEvent

    data object OrderProducts : RecommendProductErrorEvent
}
