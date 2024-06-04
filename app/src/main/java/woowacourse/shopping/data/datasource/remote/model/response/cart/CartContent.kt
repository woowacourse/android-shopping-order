package woowacourse.shopping.data.datasource.remote.model.response.cart

import woowacourse.shopping.domain.model.Product

data class CartContent(
    val id: Int,
    val product: Product,
    val quantity: Int,
)
