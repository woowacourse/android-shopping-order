package woowacourse.shopping.model.order

import woowacourse.shopping.model.product.Money

data class OrderItem(
    val productId: Long,
    val price: Money,
    val quantity: Int,
)
