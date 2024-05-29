package woowacourse.shopping.data.cart.remote

import woowacourse.shopping.domain.model.Product

data class Content(
    val id: Int,
    val product: Product,
    val quantity: Int,
)
