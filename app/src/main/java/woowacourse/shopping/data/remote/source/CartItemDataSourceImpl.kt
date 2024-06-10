package woowacourse.shopping.data.remote.source

import woowacourse.shopping.data.remote.api.ApiClient
import woowacourse.shopping.data.remote.api.CartApiService
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemRequest
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.data.source.CartItemDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.utils.DtoMapper.toCartItemList

class CartItemDataSourceImpl(apiClient: ApiClient) : CartItemDataSource {
    private val cartApiService: CartApiService = apiClient.createService(CartApiService::class.java)

    override suspend fun loadCartItems(): Result<List<CartItem>> {
        return runCatching {
            cartApiService.requestCartItems(
                page = DEFAULT_ITEM_OFFSET,
                size = MAX_CART_ITEM_SIZE,
            ).toCartItemList()
        }
    }

    override suspend fun loadCartItems(
        page: Int,
        size: Int,
    ): Result<CartItemResponse> {
        return Result.success(
            cartApiService.requestCartItems(
                page = page,
                size = size,
            ),
        )
    }

    override suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit> {
        return Result.runCatching {
            cartApiService.insertCartItem(
                cartItemRequest =
                    CartItemRequest(
                        productId = productId,
                        quantity = quantity,
                    ),
            )
        }
    }

    override suspend fun deleteCartItem(id: Int): Result<Unit> {
        return Result.runCatching { cartApiService.deleteCartItem(id = id) }
    }

    override suspend fun updateCartItem(
        id: Int,
        quantity: Int,
    ): Result<Unit> {
        return Result.runCatching {
            cartApiService.updateCartItem(
                id = id,
                quantity = CartItemQuantityDto(quantity),
            )
        }
    }

    override suspend fun loadCartItemCount(): Result<CartItemQuantityDto> {
        return Result.runCatching { cartApiService.requestCartItemCount() }
    }

    companion object {
        private const val MAX_CART_ITEM_SIZE = 50
        private const val DEFAULT_ITEM_OFFSET = 0
    }
}
