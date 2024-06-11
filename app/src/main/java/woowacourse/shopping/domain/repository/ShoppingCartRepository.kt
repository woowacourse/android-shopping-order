package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItemResult
import woowacourse.shopping.domain.model.cart.UpdateCartItemResult
import woowacourse.shopping.domain.model.cart.UpdateCartItemType
import woowacourse.shopping.domain.model.product.Product

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
