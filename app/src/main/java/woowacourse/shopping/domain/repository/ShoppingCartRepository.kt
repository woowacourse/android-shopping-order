package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Carts

interface ShoppingCartRepository {
    suspend fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Int>

    suspend fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<Carts>

    suspend fun getCartProductsQuantity(): Result<Int>

    suspend fun deleteCartProductById(cartId: Int): Result<Unit>

    suspend fun getAllCarts(): Result<Carts>
}
