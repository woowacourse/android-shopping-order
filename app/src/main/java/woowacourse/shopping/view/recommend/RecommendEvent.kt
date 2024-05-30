package woowacourse.shopping.view.recommend

interface RecommendEvent {
    sealed interface ErrorEvent : RecommendEvent {
        data object NotKnownError : ErrorEvent
    }

    sealed interface SuccessEvent : RecommendEvent

    sealed interface UpdateProductEvent : RecommendEvent {
        data class Success(val productId: Long) : UpdateProductEvent, SuccessEvent

        data object Fail : UpdateProductEvent, ErrorEvent
    }
}
