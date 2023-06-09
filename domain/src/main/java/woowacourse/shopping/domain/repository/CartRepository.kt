package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.ShoppingProduct

interface CartRepository {
    fun getAll(onSuccess: (List<CartProduct>) -> Unit, onFailure: (String) -> Unit)

    fun addCartProduct(productId: Int, quantity: Int, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit)

    fun deleteCartProduct(cartProduct: CartProduct, onSuccess: () -> Unit, onFailure: (String) -> Unit)

    fun deleteCartProduct(shoppingProduct: ShoppingProduct, onSuccess: () -> Unit, onFailure: (String) -> Unit)

    fun updateCartProductQuantity(cartProduct: CartProduct, onSuccess: () -> Unit, onFailure: (String) -> Unit)

    fun findByProductId(productId: Int, onSuccess: (CartProduct?) -> Unit, onFailure: (String) -> Unit)
}
