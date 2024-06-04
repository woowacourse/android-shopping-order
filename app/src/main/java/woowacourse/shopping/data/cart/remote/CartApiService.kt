package woowacourse.shopping.data.cart.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.request.CartSaveRequest
import woowacourse.shopping.data.dto.request.CartUpdateRequest
import woowacourse.shopping.data.dto.response.CartQuantityResponse
import woowacourse.shopping.data.dto.response.CartResponse

interface CartApiService {
    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<CartResponse>

    @POST("/cart-items")
    fun requestAddCartItems(
        @Body cartRequest: CartSaveRequest,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun requestUpdateCartItems(
        @Path(value = "id") cartId: Int,
        @Body request: CartUpdateRequest,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun requestDeleteCartItems(
        @Path(value = "id") cartId: Int,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun requestCartItemsCount(): Call<CartQuantityResponse>
}
