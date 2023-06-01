package woowacourse.shopping.data.order

import woowacourse.shopping.domain.Order

data class OrderDto(
    val id: Long,
    val totalPrice: Int,
    val cartItems: List<OrderItemDto>
) {

    fun toDomain(): Order {
        return Order(id, totalPrice, cartItems.map(OrderItemDto::toDomain))
    }
}
