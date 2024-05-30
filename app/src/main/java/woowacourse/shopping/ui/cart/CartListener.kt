package woowacourse.shopping.ui.cart

interface CartListener {
    fun deleteCartItem(productId: Int)

    fun increaseQuantity(productId: Int)

    fun decreaseQuantity(productId: Int)

    fun selectCartItem(
        productId: Int,
        isSelected: Boolean,
    )

    fun selectAllCartItem(isChecked: Boolean)
}
