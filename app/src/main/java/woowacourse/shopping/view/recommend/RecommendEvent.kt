package woowacourse.shopping.view.recommend

import woowacourse.shopping.domain.model.Product

interface RecommendEvent {
    sealed interface UpdateProductEvent : RecommendEvent {
        data class Success(val product: Product) : UpdateProductEvent
    }

    sealed interface OrderRecommends : RecommendEvent {
        data object Success : OrderRecommends
    }
}
