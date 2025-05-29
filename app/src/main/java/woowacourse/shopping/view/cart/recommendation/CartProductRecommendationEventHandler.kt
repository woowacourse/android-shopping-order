package woowacourse.shopping.view.cart.recommendation

import woowacourse.shopping.view.util.product.ProductViewHolder

interface CartProductRecommendationEventHandler : ProductViewHolder.EventHandler {
    fun onOrderClick()
}
