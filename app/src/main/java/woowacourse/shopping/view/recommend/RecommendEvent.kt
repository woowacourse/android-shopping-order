package woowacourse.shopping.view.recommend

import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.view.cart.model.ShoppingCart

interface RecommendEvent {
    sealed interface UpdateProductEvent : RecommendEvent {
        data class Success(val product: Product) : UpdateProductEvent
    }

    sealed interface OrderRecommends : RecommendEvent {
        data class Success(val shoppingCart: ShoppingCart) : OrderRecommends
    }
}
