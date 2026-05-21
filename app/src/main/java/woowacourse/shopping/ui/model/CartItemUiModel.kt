package woowacourse.shopping.ui.model

data class CartItemUiModel(
    val id: Long,
    val product: ProductUiModel,
    val quantity: Int,
    val totalPrice: Long,
    val isChecked: Boolean = false,
)
