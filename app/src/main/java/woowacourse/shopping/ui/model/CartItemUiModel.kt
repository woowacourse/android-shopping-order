package woowacourse.shopping.ui.model

data class CartItemUiModel(
    val id: String,
    val product: ProductUiModel,
    val quantity: Int,
    val totalPrice: Long,
)
