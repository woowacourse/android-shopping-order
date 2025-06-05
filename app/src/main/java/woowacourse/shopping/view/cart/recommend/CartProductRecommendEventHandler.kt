package woowacourse.shopping.view.cart.recommend

import woowacourse.shopping.view.util.product.ProductViewHolder

interface CartProductRecommendEventHandler : ProductViewHolder.EventHandler {
    fun onOrderClick()
}
