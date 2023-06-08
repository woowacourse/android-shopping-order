package woowacourse.shopping.data.remote.dto

data class OrderSubmitDTO(
    val orderId: Int?,
    val orderedDateTime: String?,
    val products: List<ProductWithQuantityDTO?>?,
    val totalPrice: Int?,
) {
    val isNotNull: Boolean
        get() = orderId != null &&
            orderedDateTime != null &&
            products != null &&
            products.all { it?.isNotNull ?: false } &&
            totalPrice != null
}
