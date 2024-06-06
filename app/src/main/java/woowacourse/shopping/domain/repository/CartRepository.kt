package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.dto.CartQuantityDto
import woowacourse.shopping.data.dto.CartResponse
import woowacourse.shopping.domain.model.CartItem

interface CartRepository {
    fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<List<CartItem>>

    fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Int>

    fun deleteCartItem(cartItemId: Int): Result<Unit>

    fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit>

    fun getCartTotalQuantity(): Result<Int>
}
