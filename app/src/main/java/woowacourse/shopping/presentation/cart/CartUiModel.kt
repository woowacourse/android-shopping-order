package woowacourse.shopping.presentation.cart

import com.example.domain.model.CartItem
import com.example.domain.model.Product
import com.example.domain.model.Quantity

data class CartUiModel(
    val cartItemId: Int,
    val productId: Int,
    val imageUrl: String,
    val title: String,
    val price: Int,
    val quantity: com.example.domain.model.Quantity,
    val isSelected: Boolean,
) {
    fun totalPrice() = price * quantity.count

    companion object {
        fun from(
            product: com.example.domain.model.Product,
            cartItem: com.example.domain.model.CartItem,
        ): CartUiModel {
            return CartUiModel(cartItem.id, product.id, product.imageUrl, product.name, product.price, cartItem.quantity, false)
        }
    }
}
