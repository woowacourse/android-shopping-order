package woowacourse.shopping.cart.model

import woowacourse.shopping.product.catalog.model.Product

data class Content(
    val id: Long,
    val product: Product,
    val quantity: Int
)