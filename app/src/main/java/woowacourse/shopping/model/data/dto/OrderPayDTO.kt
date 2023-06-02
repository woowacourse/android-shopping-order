package woowacourse.shopping.model.data.dto

data class OrderPayDTO(
    val cartItemIds: List<Long>,
    val originalPrice: Int,
    val points: Int
)
