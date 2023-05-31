package woowacourse.shopping.data.dto

import woowacourse.shopping.domain.model.Product

data class ShoppingCartDto(
    val id: Long,
    val quantity: Int,
    val product: Product,
)
