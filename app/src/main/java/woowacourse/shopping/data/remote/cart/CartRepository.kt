package woowacourse.shopping.data.remote.cart

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.cart.CartResponse.Content
import kotlin.text.substringAfterLast

class CartRepository {
    suspend fun fetchAllCart() = CartClient.getRetrofitService().requestCart(size = Int.MAX_VALUE)

    suspend fun findCartByProductId(productId: Long): Content? {
        val response = fetchAllCart()
        val matched = response.content.find { it.product.id == productId }
        return matched
    }

    suspend fun fetchCart(page: Int) = CartClient.getRetrofitService().requestCart(page = page)

    suspend fun addToCart(cartRequest: CartRequest): Result<Long> =
        try {
            val response = CartClient.getRetrofitService().addToCart(cartRequest = cartRequest)
            if (response.isSuccessful) {
                val id = response.headers()["Location"]?.substringAfterLast("/")?.toLongOrNull() ?: 0
                Result.success(id)
            } else {
                Result.failure(Throwable("응답 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun deleteCart(id: Long): Result<Unit> =
        try {
            val response = CartClient.getRetrofitService().deleteFromCart(id = id)
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
        val response = CartClient.getRetrofitService().updateCart(id = id, cartQuantity = cartQuantity)
        if (response.isSuccessful) {
            Result.success(Result.success(Unit))
        } else {
            Result.failure(Throwable("응답 실패: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getCartCounts(
        onSuccess: (Long) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        CartClient
            .getRetrofitService()
            .getCartCounts()
            .enqueue(
                object : Callback<CartQuantity> {
                    override fun onResponse(
                        call: Call<CartQuantity?>,
                        response: Response<CartQuantity?>,
                    ) {
                        if (response.isSuccessful) {
                            val totalCount = response.body()?.quantity?.toLong() ?: 0L
                            onSuccess(totalCount)
                        } else {
                            val errorMessage =
                                buildString {
                                    append("응답 실패: ${response.code()}")
                                    response.errorBody()?.let {
                                        append(" - ${it.string()}")
                                    }
                                }
                            Log.e("CartRepository", errorMessage)
                            onError(Throwable(errorMessage))
                        }
                    }

                    override fun onFailure(
                        call: Call<CartQuantity?>,
                        t: Throwable,
                    ) {
                        Log.e("CartRepository", "장바구니 조회 실패", t)
                        onError(t)
                    }
                },
            )
    }
}
