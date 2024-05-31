package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItemId
import woowacourse.shopping.domain.model.Carts

interface ShoppingCartRepository {
    fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<CartItemId>

    fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ): Result<Unit>

    fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<Carts>

    fun getCartProductsTotal(): Result<Int>

    fun deleteCartProduct(cartId: Int): Result<Unit>

    fun getAllCarts(): Result<Carts>
}
