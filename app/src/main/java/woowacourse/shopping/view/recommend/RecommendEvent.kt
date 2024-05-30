package woowacourse.shopping.view.recommend

import woowacourse.shopping.view.products.ProductListEvent

interface RecommendEvent {
    sealed interface ErrorEvent : RecommendEvent {
        data object NotKnownError : ErrorEvent
    }

    sealed interface SuccessEvent: RecommendEvent

    sealed interface UpdateProductEvent : RecommendEvent {
        data class Success(val productId: Long) : UpdateProductEvent, SuccessEvent

        data object Fail : UpdateProductEvent, ErrorEvent
    }
}
