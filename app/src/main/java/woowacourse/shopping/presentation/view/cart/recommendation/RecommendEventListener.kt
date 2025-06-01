package woowacourse.shopping.presentation.view.cart.recommendation

import woowacourse.shopping.presentation.model.ProductUiModel

interface RecommendEventListener {
    fun onInitialAddToCart(product: ProductUiModel)
}
