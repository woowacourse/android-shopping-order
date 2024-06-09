package woowacourse.shopping.data.cart.remote

import woowacourse.shopping.data.cart.remote.datasource.CartItemDataSource
import woowacourse.shopping.data.common.ApiResponseHandler.handleResponseResult
import woowacourse.shopping.data.common.ApiResponseHandler.onSuccess
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.data.cart.remote.dto.CartItemDto.Companion.toDomain
import woowacourse.shopping.ui.model.CartItem

class DefaultCartItemRepository(
    private val cartItemDataSource: CartItemDataSource,
) : CartItemRepository {
    override suspend fun loadCartItems(): ResponseResult<List<CartItem>> =
        handleResponseResult(cartItemDataSource.fetchCartItems()) { response ->
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
        handleResponseResult(cartItemDataSource.deleteCartItem(cartItemId)) { ResponseResult.Success(Unit) }

    override suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit> =
        handleResponseResult(cartItemDataSource.updateCartItemQuantity(cartItemId, quantity)) { ResponseResult.Success(Unit) }

    override suspend fun calculateCartItemsCount(): ResponseResult<Int> =
        handleResponseResult(cartItemDataSource.fetchCartItems()) { response ->
            val cartItemsCount: Int = response.content.sumOf { it.quantity }
            ResponseResult.Success(cartItemsCount)
        }
}
