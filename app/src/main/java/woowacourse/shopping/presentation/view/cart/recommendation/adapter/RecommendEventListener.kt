package woowacourse.shopping.presentation.view.cart.recommendation.adapter

import woowacourse.shopping.presentation.model.ProductUiModel

interface RecommendEventListener {
    fun onInitialAddToCart(product: ProductUiModel)
}
