package woowacouse.shopping.data.repository.cart

import woowacouse.shopping.model.cart.CartProduct

interface CartRepository {
    fun addLocalCart(cartId: Long)
    fun deleteLocalCart(cartId: Long)
    fun deleteLocalCarts(cartIds: List<Long>)
    fun updateLocalCartChecked(cartId: Long, isChecked: Boolean)
    fun getAllLocalCart(): List<CartProduct>

    fun addCartProduct(
        productId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (Long) -> Unit,
    )
    fun loadAllCarts(
        onFailure: (message: String) -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    )
    fun loadCartsByCartIds(
        cartIds: ArrayList<Long>,
        onFailure: (message: String) -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    )
    fun updateCartCount(
        cartProduct: CartProduct,
        onFailure: (message: String) -> Unit,
        onSuccess: () -> Unit,
    )
    fun deleteCart(cartId: Long)
}
