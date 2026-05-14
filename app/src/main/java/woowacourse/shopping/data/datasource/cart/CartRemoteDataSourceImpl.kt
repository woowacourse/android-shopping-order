package woowacourse.shopping.data.datasource.cart

import android.util.Log
import woowacourse.shopping.data.remote.api.AddCartItemRequest
import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.api.OrderApi
import woowacourse.shopping.data.remote.api.OrderRequest
import woowacourse.shopping.data.remote.api.UpdateCartItemRequest
import woowacourse.shopping.data.remote.dto.CartResponseDto

class CartRemoteDataSourceImpl(
    private val cartApi: CartApi,
    private val orderApi: OrderApi,
) : CartRemoteDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): CartResponseDto = cartApi.getCartItems(page, size)

    override suspend fun getCartItemsCount(): Int = cartApi.getCartItemsCount()

    override suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ) = cartApi.addCartItem(AddCartItemRequest(productId, quantity))

    override suspend fun deleteCartItem(id: Int) = cartApi.deleteCartItem(id)

    override suspend fun updateCartItem(
        id: Int,
        quantity: Int,
    ) {
        Log.d("CartRemoteDataSourceImpl", "updateCartItem: $id")
        cartApi.updateCartItem(id, UpdateCartItemRequest(quantity))
    }

    override suspend fun order(cartItemIds: List<Int>) = orderApi.order(OrderRequest(cartItemIds))
}
