package woowacourse.shopping.data.order

import woowacourse.shopping.data.product.Product

data class OrderItem(
    val quantity: Int,
    val product: Product
)
