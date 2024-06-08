package woowacourse.shopping.domain.repository.cart

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.ui.model.CartItem

interface CartItemRepository {
    suspend fun loadCartItems(): ResponseResult<List<CartItem>>

    suspend fun updateProductQuantity(
        productId: Long,
        quantity: Int,
    )

    suspend fun delete(cartItemId: Long): ResponseResult<Unit>

    suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit>

    suspend fun calculateCartItemsCount(): ResponseResult<Int>
}
