package woowacourse.shopping.data.datasource

import okhttp3.ResponseBody
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.common.PageableResponse

interface CartRemoteDataSource {
    fun fetchCartItems(
        page: Int,
        size: Int,
    ): Result<PageableResponse<CartItemResponse>>

    fun addCartItem(addCartItemCommand: AddCartItemCommand): Result<ResponseBody>

    fun deleteCartItem(cartId: Long): Result<ResponseBody>

    fun patchCartItemQuantity(
        cartId: Long,
        quantity: Quantity,
    ): Result<ResponseBody>

    fun fetchCartItemCount(): Result<Quantity>
}
