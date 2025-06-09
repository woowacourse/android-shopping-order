package woowacourse.shopping.data.datasource

import woowacourse.shopping.domain.model.CartProduct

interface CartLocalDataSource {
    val cachedCartProducts: List<CartProduct>

    fun addAll(cartProducts: List<CartProduct>)

    fun addCartProductToCart(cartProduct: CartProduct)

    fun deleteCartProductFromCartByCartId(cartId: Long)

    fun findCartProductByProductId(productId: Long): CartProduct?

    fun findCartProductByCartId(cartId: Long): CartProduct?

    fun findQuantityByProductId(productId: Long): Int

    fun findCartIdByProductId(productId: Long): Long

    fun updateQuantityByProductId(
        productId: Long,
        quantity: Int,
    )
}
