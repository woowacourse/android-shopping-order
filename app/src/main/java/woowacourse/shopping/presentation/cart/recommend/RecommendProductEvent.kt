package woowacourse.shopping.presentation.cart.recommend

sealed interface RecommendProductEvent {
    data object IncreaseCartProduct : RecommendProductEvent
    data object DecreaseCartProduct : RecommendProductEvent
    data object DeleteCartProduct : RecommendProductEvent
    data object OrderProducts : RecommendProductEvent
}