package woowacourse.shopping.remote.service

import retrofit2.Response
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.RetrofitModule
import woowacourse.shopping.remote.dto.request.CartItemRequest
import woowacourse.shopping.remote.dto.request.UpdateCartCountRequest
import woowacourse.shopping.remote.dto.response.CartCountResponse
import woowacourse.shopping.remote.dto.response.CartItemsResponse

interface CartService {
    @GET("cart-items")
    suspend fun fetchCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<CartItemsResponse>

    @POST("cart-items")
    suspend fun createCartItems(
        @Body body: CartItemRequest,
    ): Response<Unit>

    @DELETE("cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Long,
    ): Response<Unit>

    @PATCH("cart-items/{id}")
    suspend fun patchCartItem(
        @Path("id") id: Long,
        @Body body: UpdateCartCountRequest,
    ): Response<Unit>

    @GET("cart-items/counts")
    suspend fun fetchCartItemCount(): Response<CartCountResponse>

    companion object {
        private var instance: CartService? = null

        fun instance(): CartService =
            instance ?: synchronized(this) {
                instance ?: RetrofitModule.retrofit().create<CartService>()
                    .also { instance = it }
            }
    }
}
