package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Carts

interface ShoppingCartRepository {
    fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Int>

    fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ): Result<Unit>

    fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<Carts>

    fun getCartProductsQuantity(): Result<Int>

    fun deleteCartProductById(cartId: Int): Result<Unit>

    fun getAllCarts(): Result<Carts>
}
