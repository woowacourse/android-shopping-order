package woowacourse.shopping.data.remote.source

import woowacourse.shopping.data.mapper.CartItemDtoMapper.toCartItems
import woowacourse.shopping.data.mapper.CartItemDtoMapper.toQuantity
import woowacourse.shopping.data.remote.api.CartApiService
import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemRequest
import woowacourse.shopping.data.source.CartItemDataSource
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.utils.exception.ErrorEvent

class RemoteCartItemDataSourceImpl(
    private val cartApiService: CartApiService = NetworkManager.cartService(),
) : CartItemDataSource {
    override suspend fun loadCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartItem>> {
        return runCatching {
            cartApiService.requestCartItems(
                page = page,
                size = size,
            ).toCartItems()
        }
    }

    override suspend fun loadCartItem(productId: Long): Result<CartItem> {
        return runCatching {
            cartApiService.requestCartItems(
                page = DEFAULT_ITEM_OFFSET,
                size = MAX_CART_ITEM_SIZE,
            ).toCartItems().find { it.product.id == productId } ?: throw ErrorEvent.LoadDataEvent()
        }
    }

    override suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit> {
        return runCatching {
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
        return runCatching {
            cartApiService.deleteCartItem(id = id)
        }
    }

    override suspend fun updateCartItem(
        id: Int,
        quantity: Int,
    ): Result<Unit> {
        return runCatching {
            cartApiService.updateCartItem(
                id = id,
                quantity = CartItemQuantityDto(quantity),
            )
        }
    }

    override suspend fun loadCartItemCount(): Result<Int> {
        return runCatching {
            cartApiService.requestCartItemCount().toQuantity()
        }
    }

    companion object {
        private const val MAX_CART_ITEM_SIZE = 50
        private const val DEFAULT_ITEM_OFFSET = 0
    }
}
