package woowacourse.shopping.view.recommend

import woowacourse.shopping.domain.model.Product

interface RecommendEvent {
    sealed interface ErrorEvent : RecommendEvent {
        data object NotKnownError : ErrorEvent
    }

    sealed interface SuccessEvent : RecommendEvent

    sealed interface UpdateProductEvent : RecommendEvent {
        data class Success(val product: Product) : UpdateProductEvent, SuccessEvent

        data object Fail : UpdateProductEvent, ErrorEvent
    }

    sealed interface OrderRecommends : RecommendEvent {
        data object Success : OrderRecommends, SuccessEvent
        data object Fail : OrderRecommends , ErrorEvent
    }
}
