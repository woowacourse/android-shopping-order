package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.remote.CartItemIdDto
import woowacourse.shopping.data.model.remote.CartsDto

interface ShoppingCartDataSource {
    fun postCartItem(
        productId: Long,
        quantity: Int,
    ): Result<CartItemIdDto>

    fun patchCartItem(
        cartId: Int,
        quantity: Int,
    ): Result<Unit>

    fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<CartsDto>

    fun getCartProductTotalElements(): Result<Int>

    fun getCartItemsCount(): Result<Int>

    fun deleteCartItem(cartId: Int): Result<Unit>
}
