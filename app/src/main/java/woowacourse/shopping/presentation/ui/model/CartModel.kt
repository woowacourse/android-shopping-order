package woowacourse.shopping.presentation.ui.model

import woowacourse.shopping.domain.Cart

data class CartModel(
    val cartId: Long,
    val productId: Long,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val quantity: Int,
    val isChecked: Boolean,
) {
    val calculatedPrice: Int
        get() = (price * quantity).toInt()
}

fun Cart.toUiModel(isChecked: Boolean = false) =
    CartModel(cartId, product.id, product.name, product.imgUrl, product.price, quantity, isChecked)
