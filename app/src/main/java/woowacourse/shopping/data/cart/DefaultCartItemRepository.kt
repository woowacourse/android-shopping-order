package woowacourse.shopping.data.cart

import woowacourse.shopping.data.common.ResponseHandlingUtils.handle
import woowacourse.shopping.data.common.ResponseHandlingUtils.onSuccess
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.remote.cart.CartItemDto.Companion.toDomain
import woowacourse.shopping.ui.model.CartItem

class DefaultCartItemRepository(
    private val cartItemDataSource: CartItemDataSource,
) : CartItemRepository {
    override suspend fun loadCartItems(): ResponseResult<List<CartItem>> =
        handle(cartItemDataSource.fetchCartItems()) { response ->
            val cartItems: List<CartItem> = response.content.map { cartItemDto -> cartItemDto.toDomain() }
            ResponseResult.Success(cartItems)
        }

    override suspend fun updateProductQuantity(
        productId: Long,
        quantity: Int,
    ) {
        loadCartItems().onSuccess { cartItems ->
            cartItems.find { it.product.id == productId }?.let { cartItem ->
                cartItemDataSource.updateCartItemQuantity(cartItem.id, quantity)
            } ?: cartItemDataSource.saveCartItem(ProductIdsCount(productId, quantity))
        }
    }

    override suspend fun delete(cartItemId: Long): ResponseResult<Unit> =
        handle(cartItemDataSource.deleteCartItem(cartItemId)) { ResponseResult.Success(Unit) }

    override suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit> =
        handle(cartItemDataSource.updateCartItemQuantity(cartItemId, quantity)) { ResponseResult.Success(Unit) }

    override suspend fun calculateCartItemsCount(): ResponseResult<Int> =
        handle(cartItemDataSource.fetchCartItems()) { response ->
            val cartItemsCount: Int = response.content.sumOf { it.quantity }
            ResponseResult.Success(cartItemsCount)
        }
}
