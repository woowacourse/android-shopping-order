package woowacourse.shopping.data.cart

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
        when(val response = cartItemDataSource.fetchCartItems()) {
            is ResponseResult.ServerError -> ResponseResult.ServerError(response.code, "서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> ResponseResult.Exception(response.e)
            is ResponseResult.Success -> {
                val cartItems: List<CartItem> = response.data.content.map { cartItemDto -> cartItemDto.toDomain() }
                ResponseResult.Success(cartItems)
            }
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
        when(val response = cartItemDataSource.deleteCartItem(cartItemId)) {
            is ResponseResult.ServerError -> ResponseResult.ServerError(response.code, "서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> ResponseResult.Exception(response.e)
            is ResponseResult.Success -> ResponseResult.Success(response.data)
        }

    override suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit> =
        when(val response = cartItemDataSource.updateCartItemQuantity(cartItemId, quantity)) {
            is ResponseResult.ServerError -> ResponseResult.ServerError(response.code, "서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> ResponseResult.Exception(response.e)
            is ResponseResult.Success -> ResponseResult.Success(response.data)
        }

    override suspend fun calculateCartItemsCount(): ResponseResult<Int> =
        when(val response = cartItemDataSource.fetchCartItems()) {
            is ResponseResult.ServerError -> ResponseResult.ServerError(response.code, "서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> ResponseResult.Exception(response.e)
            is ResponseResult.Success -> {
                val cartItemsCount: Int = response.data.content.sumOf { it.quantity }
                ResponseResult.Success(cartItemsCount)
            }
        }
}
