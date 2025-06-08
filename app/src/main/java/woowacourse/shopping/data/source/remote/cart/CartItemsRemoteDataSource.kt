package woowacourse.shopping.data.source.remote.cart

import woowacourse.shopping.data.model.CartItemResponse
import woowacourse.shopping.data.model.CartRequest
import woowacourse.shopping.data.model.ItemCount
import woowacourse.shopping.data.source.remote.api.CartApiService
import woowacourse.shopping.data.source.remote.util.safeApiCall

class CartItemsRemoteDataSource(
    private val api: CartApiService,
) : CartItemsDataSource {
    override suspend fun getCartItems(
        page: Int?,
        size: Int?,
    ): Result<CartItemResponse> = safeApiCall {
        api.getCartItems(page = page, size = size)
    }

    override suspend fun addCartItem(
        id: Long,
        quantity: Int,
    ): Result<Long> = runCatching {
        val request = CartRequest(productId = id, quantity = quantity)
        val response = api.postCartItems(request = request)
        val locationHeader = response.headers()["Location"]
        val cartId = locationHeader?.substringAfterLast("/")?.toLongOrNull()
        requireNotNull(cartId)
    }

    override suspend fun deleteCartItem(id: Long): Result<Unit> = safeApiCall {
        api.deleteCartItems(id = id)
    }

    override suspend fun updateCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit> = safeApiCall {
        api.patchCartItems(id = id, quantity = quantity)
    }

    override suspend fun getCartCount(): Result<ItemCount> = safeApiCall {
        api.getCartItemsCounts()
    }
}
