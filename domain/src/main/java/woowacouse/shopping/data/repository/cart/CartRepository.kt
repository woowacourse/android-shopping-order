package woowacouse.shopping.data.repository.cart

import woowacouse.shopping.model.cart.CartProduct

interface CartRepository {
    fun deleteCarts(cartIds: List<Long>)
    fun updateCartChecked(cartId: Long, isChecked: Boolean)
    fun loadAllCartChecked(): List<CartProduct>

    fun addCartProduct(
        productId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Long) -> Unit,
    )
    fun loadAllCarts(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    )
    fun loadCartsByCartIds(
        cartIds: ArrayList<Long>,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    )
    fun updateCartCount(
        cartProduct: CartProduct,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: () -> Unit,
    )
    fun deleteCart(cartId: Long)
}
