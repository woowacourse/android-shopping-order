package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.ShoppingProduct

interface CartRepository {
    fun getAll(onSuccess: (List<CartProduct>) -> Unit, onFailure: () -> Unit)

    fun addCartProduct(productId: Int, quantity: Int, onSuccess: (Int) -> Unit, onFailure: () -> Unit)

    fun deleteCartProduct(cartProduct: CartProduct, onSuccess: () -> Unit, onFailure: () -> Unit)

    fun deleteCartProduct(shoppingProduct: ShoppingProduct, onSuccess: () -> Unit, onFailure: () -> Unit)

    fun updateCartProductQuantity(cartProduct: CartProduct, onSuccess: () -> Unit, onFailure: () -> Unit)

    fun findByProductId(productId: Int, onSuccess: (CartProduct?) -> Unit, onFailure: () -> Unit)
}
