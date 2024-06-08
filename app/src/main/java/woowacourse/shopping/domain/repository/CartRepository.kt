package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.CartDomain
import woowacourse.shopping.domain.model.CartItemDomain

interface CartRepository {
    suspend fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<CartDomain>

    suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun deleteCartItem(
        cartItemId: Int,
    ): Result<Unit>

    suspend fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun getCartTotalQuantity(): Result<Int>

    suspend fun getEntireCartData(): Result<List<CartData>>

    suspend fun getEntireCartItemsForCart(): Result<CartDomain>
    suspend fun getEntireCartItems(): Result<List<CartItemDomain>>
}
