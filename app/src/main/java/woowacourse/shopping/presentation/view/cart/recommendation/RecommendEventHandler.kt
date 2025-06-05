package woowacourse.shopping.presentation.view.cart.recommendation

import woowacourse.shopping.presentation.model.ProductUiModel

interface RecommendEventHandler {
    fun onInitialAddToCart(product: ProductUiModel)
}
