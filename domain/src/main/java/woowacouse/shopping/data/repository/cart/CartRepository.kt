package woowacouse.shopping.data.repository.cart

import woowacouse.shopping.model.cart.CartProduct

interface CartRepository {
    fun addLocalCart(cartId: Long)
    fun deleteLocalCart(cartId: Long)
    fun updateLocalCartChecked(cartId: Long, isChecked: Boolean)
    fun getAllLocalCart(): List<CartProduct>

    fun addCartProduct(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit,
    )
    fun loadAllCarts(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    )
    fun updateCartCount(
        cartProduct: CartProduct,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    )
    fun deleteCart(cartId: Long)
}
