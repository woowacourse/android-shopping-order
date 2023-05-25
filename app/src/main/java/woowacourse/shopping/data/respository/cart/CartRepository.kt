package woowacourse.shopping.data.respository.cart

import woowacourse.shopping.data.model.CartLocalEntity
import woowacourse.shopping.data.model.CartRemoteEntity

interface CartRepository {
    fun addLocalCart(cartId: Long)
    fun deleteLocalCart(cartId: Long)
    fun updateLocalCartChecked(cartId: Long, isChecked: Boolean)
    fun getAllLocalCart(): List<CartLocalEntity>

    fun addCartProduct(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    )
    fun loadAllCarts(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    )
    fun updateCartCount(
        cartEntity: CartRemoteEntity,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    )
    fun deleteCart(cartId: Long)
}
