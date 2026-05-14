package woowacourse.shopping.ui.cart.list

data class CartItemUiModel(
    val cartItemId: Long,
    val productId: Long,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val quantity: Int,
    val isSelected: Boolean = true,
)
