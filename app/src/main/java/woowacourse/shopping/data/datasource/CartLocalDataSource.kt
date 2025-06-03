package woowacourse.shopping.data.datasource

import woowacourse.shopping.domain.model.CartProduct

interface CartLocalDataSource {
    fun addCartProduct(cartProduct: CartProduct)

    fun addAllCartProducts(cartProducts: List<CartProduct>)

    fun removeCartProductByCartId(cartId: Long)

    fun updateQuantity(
        productId: Long,
        quantity: Int,
    )

    fun getQuantity(productId: Long): Int

    fun getCartProduct(productId: Long): CartProduct?

    fun getCartProducts(): List<CartProduct>
}
