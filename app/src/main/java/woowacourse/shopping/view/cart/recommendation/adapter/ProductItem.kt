package woowacourse.shopping.view.cart.recommendation.adapter

import woowacourse.shopping.domain.model.Product

data class ProductItem(
    val product: Product,
    val quantity: Int = 0,
)
