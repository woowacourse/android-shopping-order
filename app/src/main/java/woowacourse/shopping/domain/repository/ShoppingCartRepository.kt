package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product

interface ShoppingCartRepository {
    suspend fun insertCartItem(product: Product): Result<Unit>

    suspend fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>>

    suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult>

    suspend fun deleteCartItem(itemId: Long): Result<Unit>

    suspend fun increaseCartItem(product: Product): Result<Unit>

    suspend fun decreaseCartItem(product: Product): Result<Unit>

    suspend fun updateCartCount(cartItemResult: CartItemResult): Result<Unit>

    suspend fun getTotalCartItemCount(): Result<Int>
}
