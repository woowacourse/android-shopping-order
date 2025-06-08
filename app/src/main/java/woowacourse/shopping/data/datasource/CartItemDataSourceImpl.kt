package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.data.model.response.cartitem.CartItemResponse
import woowacourse.shopping.data.service.CartItemService

class CartItemDataSourceImpl(
    private val cartItemService: CartItemService,
) : CartItemDataSource {
    override suspend fun fetchPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
    ): CartItemResponse {
        return cartItemService.getCartItems(page = pageIndex, size = pageSize)
    }

    override suspend fun submitCartItem(cartItem: CartItemRequest) {
        cartItemService.postCartItem(cartItem)
    }

    override suspend fun removeCartItem(cartId: Long) {
        cartItemService.deleteCartItem(cartId)
    }

    override suspend fun updateCartItem(
        cartId: Long,
        quantity: Quantity,
    ) {
        cartItemService.patchCartItem(cartId, quantity)
    }

    override suspend fun fetchCartItemsCount(): Quantity {
        return cartItemService.getCartItemsCount()
    }
}
