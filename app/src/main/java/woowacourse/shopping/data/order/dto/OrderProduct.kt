package woowacourse.shopping.data.order.dto

import woowacourse.shopping.data.product.dto.Product
import woowacourse.shopping.model.OrderProduct

data class OrderProduct(
    val product: Product,
    val quantity: Int,
) {
    fun toDomainOrderProduct(): OrderProduct =
        OrderProduct(product.toDomainProduct(), quantity)
}
