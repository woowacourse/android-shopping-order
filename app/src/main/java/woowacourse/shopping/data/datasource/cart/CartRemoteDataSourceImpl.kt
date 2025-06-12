package woowacourse.shopping.data.datasource.cart

import retrofit2.HttpException
import woowacourse.shopping.data.api.CartApi
import woowacourse.shopping.data.model.request.CartItemQuantityRequest
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemsQuantityResponse
import woowacourse.shopping.data.model.response.CartItemsResponse

class CartRemoteDataSourceImpl(
    private val api: CartApi,
) : CartRemoteDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): CartItemsResponse {
        val response = api.getCartItems(page, size)

        return if (response.isSuccessful) {
            response.body() ?: throw IllegalStateException()
        } else {
            throw HttpException(response)
        }
    }

    override suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ) {
        val request = CartItemRequest(productId = productId, quantity = quantity)
        val response = api.postCartItem(request)

        if (!response.isSuccessful) throw HttpException(response)
    }

    override suspend fun deleteCartItem(cartId: Long) {
        val response = api.deleteCartItem(cartId)

        if (!response.isSuccessful) throw HttpException(response)
    }

    override suspend fun patchCartItem(
        cartId: Long,
        quantity: Int,
    ) {
        val request = CartItemQuantityRequest(quantity)
        val response = api.patchCartItem(cartId, request)

        if (!response.isSuccessful) throw HttpException(response)
    }

    override suspend fun getCartItemsCount(): CartItemsQuantityResponse {
        val response = api.getCartItemsCount()

        return if (response.isSuccessful) {
            response.body() ?: throw IllegalStateException()
        } else {
            throw HttpException(response)
        }
    }
}
