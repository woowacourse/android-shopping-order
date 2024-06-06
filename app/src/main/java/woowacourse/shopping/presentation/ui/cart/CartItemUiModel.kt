package woowacourse.shopping.presentation.ui.cart

data class CartItemUiModel(
    val id: Long,
    val productId: Long,
    val productName: String,
    val price: Long,
    val imgUrl: String,
    val quantity: Int,
    val isChecked: Boolean,
)
