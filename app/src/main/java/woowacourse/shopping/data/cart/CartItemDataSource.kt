package woowacourse.shopping.data.cart

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.cart.CartItemResponse

interface CartItemDataSource {
    suspend fun fetchCartItems(): ResponseResult<CartItemResponse>

    suspend fun saveCartItem(productIdsCount: ProductIdsCount): ResponseResult<Unit>

    suspend fun deleteCartItem(cartItemId: Long): ResponseResult<Unit>

    suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit>
}
