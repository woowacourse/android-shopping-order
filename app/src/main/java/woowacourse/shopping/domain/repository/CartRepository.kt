package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse

interface CartRepository {
    fun getCartResponse(
        page: Int,
        size: Int,
        sort: String,
    ): Result<CartResponse>

    fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit>

    fun deleteCartItem(cartItemId: Int): Result<Unit>

    fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit>

    fun getCartTotalQuantity(): Result<CartQuantity>
}
