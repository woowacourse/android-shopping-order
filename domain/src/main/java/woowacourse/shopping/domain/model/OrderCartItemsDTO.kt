package woowacourse.shopping.domain.model

data class OrderCartItemsDTO(val orderCartItemDtos: List<OrderCartItemDTO>) {
    data class OrderCartItemDTO(val cartItemId: Int, val orderCartItemName: String, val orderCartItemPrice: Int, val orderCartItemImageUrl: String)
}
