package woowacourse.shopping.data.cart.remote.datasource

import woowacourse.shopping.data.cart.remote.dto.CartItemResponse
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.ProductIdsCount

interface CartItemDataSource {
    suspend fun fetchCartItems(): ResponseResult<CartItemResponse>

    suspend fun saveCartItem(productIdsCount: ProductIdsCount): ResponseResult<Unit>

    suspend fun deleteCartItem(cartItemId: Long): ResponseResult<Unit>

    suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit>
}
