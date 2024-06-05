package woowacourse.shopping.data.cart.model

import woowacourse.shopping.domain.entity.Product

data class CartItemData(
    val cartId: Long,
    val count: Int,
    val product: Product,
)
