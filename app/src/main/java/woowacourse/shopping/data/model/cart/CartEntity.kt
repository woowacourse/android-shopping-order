package woowacourse.shopping.data.model.cart

data class CartEntity(
    val productId: Long,
    val count: Int,
    val isSelected: Int
)
