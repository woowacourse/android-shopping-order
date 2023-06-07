package woowacourse.shopping.data.remote.dto

data class OrdersDTO(val orders: List<OrderDTO>?) {
    val isNotNull: Boolean
        get() = orders != null && orders.all { it?.isNotNull ?: false }

    data class OrderDTO(
        val orderId: Int?,
        val orderedDateTime: String?,
        val products: List<ProductWithQuantityDTO>?,
        val totalPrice: Int?,
    ) {
        val isNotNull: Boolean
            get() = orderId != null &&
                orderedDateTime != null &&
                products != null &&
                products.all { it?.isNotNull ?: false } &&
                totalPrice != null
    }
}
