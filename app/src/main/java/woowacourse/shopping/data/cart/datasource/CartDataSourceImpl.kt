package woowacourse.shopping.data.cart.datasource

import retrofit2.Response
import woowacourse.shopping.data.cart.model.CartPageData
import woowacourse.shopping.data.cart.toData
import woowacourse.shopping.remote.dto.request.CartItemRequest
import woowacourse.shopping.remote.dto.request.UpdateCartCountRequest
import woowacourse.shopping.remote.service.CartService

class CartDataSourceImpl(
    private val cartService: CartService,
) : CartDataSource {
    override suspend fun loadCarts(
        currentPage: Int,
        productSize: Int,
    ): Result<CartPageData> {
        val response = cartService.fetchCartItems(currentPage, productSize)
        val cartPageData = response.body()?.toData() ?: throw Exception("Empty body")
        return if (response.isSuccessful) {
            Result.success(cartPageData)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    }

    override suspend fun loadTotalCarts(): Result<CartPageData> {
        val totalCountResponse = cartService.fetchCartItemCount()
        val totalCount = totalCountResponse.body()?.quantity ?: throw Exception("Empty body")
        val cartItemsResponse = cartService.fetchCartItems(0, totalCount)
        val cartPageData = cartItemsResponse.body()?.toData() ?: throw Exception("Empty body")
        return if (totalCountResponse.isSuccessful) {
            Result.success(cartPageData)
        } else {
            Result.failure(Exception("Error: ${totalCountResponse.code()} - ${totalCountResponse.message()}"))
        }
    }

    override suspend fun createCartProduct(
        productId: Long,
        count: Int,
    ): Result<Long> {
        val request = CartItemRequest(productId, count)
        val response = cartService.createCartItems(request)
        val id = response.toIdOrNull() ?: error("Failed to create cart product")
        return if (response.isSuccessful) {
            Result.success(id)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    }

    override suspend fun updateCartCount(
        cartId: Long,
        count: Int,
    ): Result<Unit> {
        val response = cartService.patchCartItem(cartId, UpdateCartCountRequest(count))
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    }

    override suspend fun deleteCartProduct(cartId: Long): Result<Unit> {
        val response = cartService.deleteCartItem(cartId)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    }

    private fun <T> Response<T>.toIdOrNull(): Long? {
        return headers()["LOCATION"]?.substringAfterLast("/")
            ?.toLongOrNull()
    }
}
