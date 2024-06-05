package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItemId
import woowacourse.shopping.domain.model.Carts

interface ShoppingCartRepository {
    suspend fun postCartItem(
        productId: Long,
        quantity: Int,
    ): Result<CartItemId>

    suspend fun patchCartItem(
        cartId: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<Carts>

    suspend fun getCartItemsCount(): Result<Int>

    suspend fun deleteCartItem(cartId: Int): Result<Unit>

    suspend fun getAllCarts(): Result<Carts>

    companion object {
        private var instance: ShoppingCartRepository? = null

        fun setInstance(shoppingCartRepository: ShoppingCartRepository) {
            instance = shoppingCartRepository
        }

        fun getInstance(): ShoppingCartRepository = requireNotNull(instance)
    }
}
