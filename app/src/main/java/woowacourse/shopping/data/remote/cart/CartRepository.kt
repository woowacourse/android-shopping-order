package woowacourse.shopping.data.remote.cart

import woowacourse.shopping.data.remote.cart.CartResponse.Content
import kotlin.text.substringAfterLast

class CartRepository(
    private val cartService: CartService,
) {
    suspend fun fetchAllCart() = cartService.requestCart(size = Int.MAX_VALUE)

    suspend fun findCartByProductId(productId: Long): Content? {
        val response = fetchAllCart()
        val matched = response.content.find { it.product.id == productId }
        return matched
    }

    suspend fun fetchCart(page: Int) = cartService.requestCart(page = page)

    suspend fun addToCart(cartRequest: CartRequest): Result<Long> =
        try {
            val response = cartService.addToCart(cartRequest = cartRequest)
            if (response.isSuccessful) {
                val id =
                    response.headers()["Location"]?.substringAfterLast("/")?.toLongOrNull() ?: 0
                Result.success(id)
            } else {
                Result.failure(Throwable("응답 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun deleteCart(id: Long): Result<Unit> =
        try {
            val response = cartService.deleteFromCart(id = id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Throwable("응답 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun updateCart(
        id: Long,
        cartQuantity: CartQuantity,
    ) = try {
        val response = cartService.updateCart(id = id, cartQuantity = cartQuantity)
        if (response.isSuccessful) {
            Result.success(Result.success(Unit))
        } else {
            Result.failure(Throwable("응답 실패: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getCartCounts(): Result<Long> =
        try {
            val response = cartService.getCartCounts()
            if (response.isSuccessful) {
                val totalCount = response.body()?.quantity?.toLong() ?: 0L
                Result.success(totalCount)
            } else {
                Result.failure(Throwable("응답 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}
