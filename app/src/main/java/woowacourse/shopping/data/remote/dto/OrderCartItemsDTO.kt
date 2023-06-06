package woowacourse.shopping.data.remote.dto

data class OrderCartItemsDTO(val orderCartItemDtos: List<OrderCartItemDTO?>?) {
    val isNotNull: Boolean
        get() = orderCartItemDtos != null && orderCartItemDtos.all { it?.isNotNull ?: false }
    data class OrderCartItemDTO(
        val cartItemId: Int?,
        val orderCartItemName: String?,
        val orderCartItemPrice: Int?,
        val orderCartItemImageUrl: String?,
    ) {
        val isNotNull: Boolean
            get() = cartItemId != null && orderCartItemName != null && orderCartItemPrice != null && orderCartItemImageUrl != null
    }
}
