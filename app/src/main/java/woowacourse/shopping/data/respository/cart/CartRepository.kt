package woowacourse.shopping.data.respository.cart

import com.example.domain.cart.CartProduct
import com.example.domain.cart.CartProducts
import woowacourse.shopping.data.model.CartLocalEntity

interface CartRepository {
    fun addLocalCart(cartId: Long)
    fun deleteLocalCart(cartId: Long)
    fun updateLocalCartChecked(cartId: Long, isChecked: Boolean)
    fun getAllLocalCart(): List<CartLocalEntity>

    fun addCartProduct(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit,
    )
    fun loadAllCarts(
        onFailure: () -> Unit,
        onSuccess: (products: CartProducts) -> Unit,
    )
    fun updateCartCount(
        cartProduct: CartProduct,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    )
    fun deleteCart(cartId: Long)
}
