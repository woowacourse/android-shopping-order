package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Product

interface CartRepository {
    fun addCartProduct(product: Product, onSuccess: (Int) -> Unit, onFailure: () -> Unit)

    fun getAllCount(onSuccess: (Int) -> Unit, onFailure: () -> Unit)

    fun getAll(onSuccess: (List<CartProduct>) -> Unit, onFailure: () -> Unit)

    fun deleteCartProduct(cartProduct: CartProduct)

    // fun getTotalAmount(): Int

    // fun getCartProductByProduct(product: Product): CartProduct?

    fun updateCartProductQuantity(cartProduct: CartProduct, onSuccess: () -> Unit, onFailure: () -> Unit)

    // fun getTotalPrice(): Int

    fun findByProductId(productId: Int, onSuccess: (CartProduct?) -> Unit, onFailure: () -> Unit)
}
