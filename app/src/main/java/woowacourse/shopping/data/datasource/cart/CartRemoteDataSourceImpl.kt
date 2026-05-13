package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.data.remote.api.AddCartItemRequest
import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.api.UpdateCartItemRequest
import woowacourse.shopping.data.remote.dto.CartItemDto

class CartRemoteDataSourceImpl(
    private val cartApi: CartApi,
) : CartRemoteDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): List<CartItemDto> = cartApi.getCartItems(page, size)

    override suspend fun getCartItemsCount(): Int = cartApi.getCartItemsCount()

    override suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ) = cartApi.addCartItem(AddCartItemRequest(productId, quantity))

    override suspend fun deleteCartItem(id: Int) = cartApi.deleteCartItem(id)

    override suspend fun updateCartItem(
        id: Int,
        quantity: Int,
    ) = cartApi.updateCartItem(id, UpdateCartItemRequest(quantity))
}
