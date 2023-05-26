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
        }
    }

    fun getCart(cartId: Long): CartProduct? =
        carts.find { it.id == cartId }

    fun getAll(): List<CartProduct> = carts.toList()
}
