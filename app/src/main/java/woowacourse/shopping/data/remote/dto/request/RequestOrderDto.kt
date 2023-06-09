package woowacourse.shopping.data.remote.dto.request

data class RequestOrderDto(
    val orderCartItemDtos: List<OrderItemDto>,
) {
    data class OrderItemDto(
        val cartItemId: Long,
        val orderCartItemName: String,
        val orderCartItemPrice: Int,
        val orderCartItemImageUrl: String,
    )
}
