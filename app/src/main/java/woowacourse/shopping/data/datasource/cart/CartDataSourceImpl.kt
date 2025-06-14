package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.data.model.response.cartitem.CartItemContent
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Page

class CartDataSourceImpl(
    private val cartItemService: CartItemService,
) : CartDataSource {
    override suspend fun fetchPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
    ): Page<CartItem> {
        val response = cartItemService.getCartItems(page = pageIndex, size = pageSize)
        return Page(
            response.content.map(CartItemContent::toCartItem),
            response.first,
            response.last,
        )
    }

    override suspend fun submitCartItem(
        productId: Long,
        quantity: Int,
    ) {
        cartItemService.postCartItem(CartItemRequest(productId, quantity))
    }

    override suspend fun removeCartItem(cartId: Long) {
        cartItemService.deleteCartItem(cartId)
    }

    override suspend fun removeCartItems(cartIds: List<Long>) {
        cartIds.forEach { cartId -> cartItemService.deleteCartItem(cartId) }
    }

    override suspend fun updateCartItem(
        cartId: Long,
        quantity: Int,
    ) {
        cartItemService.patchCartItem(cartId, Quantity(quantity))
    }

    override suspend fun fetchCartItemsCount(): Int {
        return cartItemService.getCartItemsCount().quantity
    }
}
