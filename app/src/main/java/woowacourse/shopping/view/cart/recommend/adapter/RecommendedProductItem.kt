package woowacourse.shopping.view.cart.recommend.adapter

import woowacourse.shopping.domain.model.Product

data class RecommendedProductItem(
    val product: Product,
    val quantity: Int = 0,
)
