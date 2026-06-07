package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.data.remote.api.AddCartItemRequest
import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.api.OrderApi
import woowacourse.shopping.data.remote.api.OrderRequest
import woowacourse.shopping.data.remote.api.UpdateCartItemRequest
import woowacourse.shopping.data.remote.dto.CartResponseDto
import woowacourse.shopping.domain.model.cart.Quantity

class CartRemoteDataSourceImpl(
    private val cartApi: CartApi,
    private val orderApi: OrderApi,
) : CartDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): CartResponseDto = cartApi.getCartItems(page, size)

    override suspend fun getCartItemsCount(): Int = cartApi.getCartItemsCount().quantity

    override suspend fun addCartItem(
        productId: Int,
        quantity: Quantity,
    ) = cartApi.addCartItem(AddCartItemRequest(productId, quantity.value))

    override suspend fun deleteCartItem(id: Int) = cartApi.deleteCartItem(id)

    override suspend fun updateCartItem(
        id: Int,
        quantity: Quantity,
    ) {
        cartApi.updateCartItem(id, UpdateCartItemRequest(quantity.value))
    }

    override suspend fun order(cartItemIds: List<Int>) = orderApi.order(OrderRequest(cartItemIds))
}
