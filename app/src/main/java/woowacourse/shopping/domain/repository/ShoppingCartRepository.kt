package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItemId
import woowacourse.shopping.domain.model.Carts

interface ShoppingCartRepository {
    fun postCartItem(
        productId: Long,
        quantity: Int,
    ): Result<CartItemId>

    fun patchCartItem(
        cartId: Int,
        quantity: Int,
    ): Result<Unit>

    fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<Carts>

    fun getCartItemsCount(): Result<Int>

    fun deleteCartItem(cartId: Int): Result<Unit>

    fun getAllCarts(): Result<Carts>

    companion object {
        private var instance: ShoppingCartRepository? = null

        fun setInstance(shoppingCartRepository: ShoppingCartRepository) {
            instance = shoppingCartRepository
        }

        fun getInstance(): ShoppingCartRepository = requireNotNull(instance)
    }
}
