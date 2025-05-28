package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.common.PageableResponse

interface CartRemoteDataSource {
    fun fetchCartItems(
        page: Int,
        size: Int,
    ): Result<PageableResponse<CartItemResponse>>

    fun addCartItem(addCartItemCommand: AddCartItemCommand): Result<Long>

    fun deleteCartItem(cartId: Long): Result<Unit>

    fun patchCartItemQuantity(
        cartId: Long,
        quantity: Quantity,
    ): Result<Unit>

    fun fetchCartItemCount(): Result<Quantity>
}
