package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.common.PageableResponse

interface CartRemoteDataSource {
    suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ): PageableResponse<CartItemResponse>

    suspend fun addCartItem(addCartItemCommand: AddCartItemCommand): Long

    suspend fun deleteCartItem(cartId: Long)

    suspend fun patchCartItemQuantity(
        cartId: Long,
        quantity: Quantity,
    )

    suspend fun fetchCartItemCount(): Quantity
}
