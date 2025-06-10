package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.domain.model.CartProduct

interface CartLocalDataSource {
    fun addCartProduct(cartProduct: CartProduct): Result<Unit>

    fun addAllCartProducts(cartProducts: List<CartProduct>): Result<Unit>

    fun removeCartProductByCartId(cartId: Long): Result<Unit>

    fun removeCartProductsByCartIds(cartIds: List<Long>): Result<Unit>

    fun updateQuantity(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    fun getQuantity(productId: Long): Result<Int>

    fun getCartProduct(productId: Long): Result<CartProduct?>

    fun getCartProducts(): Result<List<CartProduct>>
}
