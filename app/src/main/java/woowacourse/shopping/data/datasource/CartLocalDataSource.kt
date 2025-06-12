package woowacourse.shopping.data.datasource

import woowacourse.shopping.domain.model.CartProduct

interface CartLocalDataSource {
    val cachedCartProducts: List<CartProduct>

    fun addAll(cartProducts: List<CartProduct>)

    fun addCartProductToCart(cartProduct: CartProduct)

    fun deleteCartProductFromCartByCartId(cartId: Long)

    fun fetchCartProductByProductId(productId: Long): CartProduct

    fun fetchCartProductByCartId(cartId: Long): CartProduct

    fun fetchQuantityByProductId(productId: Long): Int

    fun fetchCartIdByProductId(productId: Long): Long

    fun updateQuantityByProductId(
        productId: Long,
        quantity: Int,
    )
}
