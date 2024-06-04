package woowacourse.shopping.data.source

import retrofit2.Call
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemResult

interface CartItemDataSource {
    fun loadCartItems(): Result<CartItemResponse>

    fun loadCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartItem>>

    fun loadCartItemResult(
        productId: Long,
    ): Result<CartItemResult>

    fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit>

    fun deleteCartItem(id: Int): Result<Unit>

    fun updateCartItem(
        id: Int,
        quantity: Int,
    ): Result<Unit>

    fun loadCartItemCount(): Result<Int>
}
