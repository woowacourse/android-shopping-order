package woowacouse.shopping.model.cart

class CartProducts(
    carts: List<CartProduct>
) {
    private val carts: MutableList<CartProduct> = carts.toMutableList()

    fun addCart(cartProduct: CartProduct) {
        carts.add(cartProduct)
    }

    fun deleteCart(cartId: Long) {
        carts.removeIf { it.id == cartId }
    }

    fun updateCartChecked(cartId: Long) {
        carts.find { it.id == cartId }?.apply {
            carts.remove(this)
            carts.add(updateCartChecked())
        } ?: throw IllegalArgumentException(
            ERROR_NOT_EXITS_CART_ID
        )
    }

    fun getCart(cartId: Long): CartProduct =
        carts.find { it.id == cartId } ?: throw IllegalArgumentException(
            ERROR_NOT_EXITS_CART_ID
        )

    fun getAll(): List<CartProduct> = carts.toList()

    companion object {
        private const val ERROR_NOT_EXITS_CART_ID = "존재하지 않는 카트 ID입니다"
    }
}
