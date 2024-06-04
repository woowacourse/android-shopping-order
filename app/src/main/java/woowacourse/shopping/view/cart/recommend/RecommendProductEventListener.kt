package woowacourse.shopping.view.cart.recommend

import woowacourse.shopping.domain.model.ProductItemDomain

interface RecommendProductEventListener {
    fun navigateToDetail(productId: Int)

    fun navigateToCart()

    fun addToCart(product: ProductItemDomain)
}
