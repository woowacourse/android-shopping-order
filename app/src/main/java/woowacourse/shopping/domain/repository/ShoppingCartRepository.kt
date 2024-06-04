package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType

interface ShoppingCartRepository {
    suspend fun addCartItem(product: Product): Result<Unit>

    suspend fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>>

    suspend fun deleteCartItem(itemId: Long): Result<Unit>

    suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult>

    suspend fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ): Result<UpdateCartItemResult>

    suspend fun getTotalCartItemCount(): Result<Int>
}
