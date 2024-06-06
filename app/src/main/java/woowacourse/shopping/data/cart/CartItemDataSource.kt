package woowacourse.shopping.data.cart

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.cart.CartItemResponse

interface CartItemDataSource {
    fun fetchCartItems(): ResponseResult<CartItemResponse>

    fun saveCartItem(productIdsCount: ProductIdsCount): ResponseResult<Unit>

    fun deleteCartItem(cartItemId: Long): ResponseResult<Unit>

    fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit>
}
