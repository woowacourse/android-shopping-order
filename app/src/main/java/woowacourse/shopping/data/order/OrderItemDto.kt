package woowacourse.shopping.data.order

import woowacourse.shopping.domain.OrderItem

data class OrderItemDto(
    val id: Long?,
    val quantity: Int,
    val product: ProductDto
) {

    fun toDomain(): OrderItem = OrderItem(quantity, product.toDomain())
}
