package woowacourse.shopping.data.cart

import woowacourse.shopping.data.common.ResponseHandlingUtils.handleResponse
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.remote.cart.CartItemDto.Companion.toDomain
import woowacourse.shopping.ui.model.CartItem

class DefaultCartItemRepository(
    private val cartItemDataSource: CartItemDataSource,
) : CartItemRepository {
    override suspend fun loadCartItems(): List<CartItem> {
        return handleResponse(cartItemDataSource.fetchCartItems()).content.map { cartItemDto -> cartItemDto.toDomain() }
    }

    override suspend fun updateProductQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val cartItem = loadCartItems().find { it.product.id == productId }
        if (cartItem == null) {
            handleResponse(cartItemDataSource.saveCartItem(ProductIdsCount(productId, quantity)))
            return
        }
        handleResponse(cartItemDataSource.updateCartItemQuantity(cartItem.id, quantity))
    }

    override suspend fun delete(id: Long) {
        handleResponse(cartItemDataSource.deleteCartItem(id))
    }

    override suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ) {
        handleResponse(cartItemDataSource.updateCartItemQuantity(cartItemId, quantity))
    }

    override suspend fun calculateCartItemsCount(): Int = handleResponse(cartItemDataSource.fetchCartItems()).content.sumOf { it.quantity }
}
