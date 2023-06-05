package woowacourse.shopping.model.data.dto

data class OrderPayDTO(
    val cartItemIds: List<CartItemIdDTO>,
    val originalPrice: Int,
    val points: Int
)
