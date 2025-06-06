package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.common.PageableResponse

interface CartRemoteDataSource {
    suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ): Result<PageableResponse<CartItemResponse>>

    suspend fun addCartItem(addCartItemCommand: AddCartItemCommand): Result<Long>

    suspend fun deleteCartItem(cartId: Long): Result<Unit>

    suspend fun patchCartItemQuantity(
        cartId: Long,
        quantity: Quantity,
    ): Result<Unit>

    suspend fun fetchCartItemCount(): Result<Quantity>
}
