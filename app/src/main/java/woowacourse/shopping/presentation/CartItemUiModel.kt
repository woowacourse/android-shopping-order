package woowacourse.shopping.presentation

data class CartItemUiModel(
    val id: Long = 0,
    val product: ProductUiModel,
    val count: Int = 0,
    val isSelected: Boolean = true,
    val totalPrice: Int,
)
