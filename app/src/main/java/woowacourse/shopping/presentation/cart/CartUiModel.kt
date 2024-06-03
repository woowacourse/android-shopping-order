package woowacourse.shopping.presentation.cart

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity

data class CartUiModel(
    val cartItemId: Int,
    val productId: Int,
    val imageUrl: String,
    val title: String,
    val price: Int,
    val quantity: Quantity,
    val isSelected: Boolean,
) {
    fun totalPrice() = price * quantity.count

    companion object {
        fun from(
            product: Product,
            cartItem: CartItem,
        ): CartUiModel {
            return CartUiModel(cartItem.id, product.id, product.imageUrl, product.name, product.price, cartItem.quantity, false)
        }
    }
}
