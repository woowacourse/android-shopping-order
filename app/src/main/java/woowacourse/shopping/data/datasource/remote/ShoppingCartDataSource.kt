package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.remote.CartItemIdDto
import woowacourse.shopping.data.model.remote.CartsDto

interface ShoppingCartDataSource {
    suspend fun postCartItem(
        productId: Long,
        quantity: Int,
    ): Result<CartItemIdDto>

    suspend fun patchCartItem(
        cartId: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<CartsDto>

    suspend fun getCartProductTotalElements(): Result<Int>

    suspend fun getCartItemsCount(): Result<Int>

    suspend fun deleteCartItem(cartId: Int): Result<Unit>

    companion object {
        private var instance: ShoppingCartDataSource? = null

        fun setInstance(shoppingCartDataSource: ShoppingCartDataSource) {
            instance = shoppingCartDataSource
        }

        fun getInstance(): ShoppingCartDataSource = requireNotNull(instance)
    }
}
