package woowacourse.shopping.ui.cart

interface CartListener {
    fun deleteCartItem(cartItemId: Int)

    fun increaseQuantity(productId: Int)

    fun decreaseQuantity(productId: Int)

    fun selectCartItem(
        productId: Int,
        isSelected: Boolean,
    )

    fun toggleAllCartItem(isSelected: Boolean)
}
