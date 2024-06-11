package woowacourse.shopping.presentation.ui.model

data class CartModel(
    val cartId: Long,
    val productId: Long,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val quantity: Int,
    val isChecked: Boolean,
    val calculatedPrice: Int,
)
