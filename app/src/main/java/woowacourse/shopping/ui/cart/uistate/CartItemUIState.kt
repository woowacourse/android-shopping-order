package woowacourse.shopping.ui.cart.uistate

import woowacourse.shopping.domain.cart.CartItem

data class CartItemUIState(
    val id: Long,
    val productId: Long,
    val isSelected: Boolean,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val quantity: Int
) {
    companion object {
        fun CartItem.toUIState(isSelected: Boolean): CartItemUIState {
            return CartItemUIState(
                id = id,
                productId = product.id,
                isSelected = isSelected,
                imageUrl = product.imageUrl,
                name = product.name,
                price = product.price,
                quantity = quantity,
            )
        }
    }
}
