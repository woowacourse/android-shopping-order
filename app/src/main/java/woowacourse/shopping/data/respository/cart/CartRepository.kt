package woowacourse.shopping.data.respository.cart

import woowacourse.shopping.data.model.CartEntity
import woowacourse.shopping.data.model.CartEntity2

interface CartRepository {
    fun updateCartByProductId(productId: Long, count: Int, checked: Int)
    fun updateCartCountByCartId(cartId: Long, count: Int)
    fun updateCartCheckedByCartId(cartId: Long, checked: Boolean)
    fun getCarts(startPosition: Int): List<CartEntity>
    fun getAllCarts(): List<CartEntity>
    fun deleteAllCartByProductId(productId: Long)
    fun deleteCartByCartId(cartId: Long)
    fun deleteCartByProductId(productId: Long)
    fun addCart(productId: Long, count: Int)
    fun addCartProduct(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    )
    fun loadAllCarts(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartEntity2>) -> Unit,
    )
    fun updateCartCount(
        cartEntity: CartEntity2,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    )
    fun deleteCart(cartId: Long)
}
