package woowacourse.shopping.data.datasource.remote

import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.data.dto.cart.CartContent
import woowacourse.shopping.data.dto.cart.CartIdResponse
import woowacourse.shopping.data.dto.cart.CartItemCountResponse
import woowacourse.shopping.data.dto.cart.CartItemRequest
import woowacourse.shopping.data.dto.cart.UpdateCartRequest
import woowacourse.shopping.data.remote.CartItemService

class CartDataSourceImpl(
    private val cartItemService: CartItemService,
) : CartDataSource {
    override suspend fun getTotalCount(): CartItemCountResponse {
        val response = cartItemService.requestCartItemCount()
        if (response.isSuccessful) {
            return response.body() ?: CartItemCountResponse(0)
        }
        throw HttpException(response)
    }

    override suspend fun getPagedCartItems(
        page: Int,
        size: Int?,
    ): List<CartContent> {
        val response = cartItemService.requestCartItems(page, size)
        if (response.isSuccessful) {
            return response.body()?.cartContent ?: emptyList()
        }
        throw HttpException(response)
    }

    override suspend fun insertCartItem(
        productId: Long,
        quantity: Int,
    ): CartIdResponse {
        val response = cartItemService.addCartItem(CartItemRequest(productId, quantity))
        if (response.isSuccessful) {
            val id = response.toIdOrNull() ?: throw IllegalStateException(ERROR_NOT_EXIST_ID)
            return CartIdResponse(id)
        }

        throw HttpException(response)
    }

    override suspend fun updateQuantity(
        cartId: Long,
        quantity: Int,
    ) {
        val response = cartItemService.updateCartItem(cartId, UpdateCartRequest(quantity))
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }

    override suspend fun deleteCartItemById(cartId: Long) {
        val response = cartItemService.deleteCartItem(cartId)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }

    private fun <T> Response<T>.toIdOrNull(): Long? = headers()["LOCATION"]?.substringAfterLast("/")?.toLongOrNull()

    companion object {
        private const val ERROR_NOT_EXIST_ID = "ID 값이 존재하지 않습니다."
    }
}
