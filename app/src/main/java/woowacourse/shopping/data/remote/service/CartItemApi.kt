package woowacourse.shopping.data.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartResponse
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse

interface CartItemApi {
    @GET("/cart-items")
    fun getCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<CartResponse>

    @POST("/cart-items")
    fun postCartItem(
        @Header("accept") accept: String = "*/*",
        @Body cartItemRequest: CartItemRequest
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun patchCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
        @Body quantityRequest: QuantityRequest
    ): Call<Unit>

    @GET("/car-items/counts")
    fun getCartItemsCounts(
        @Header("accept") accept: String = "*/*",
    ): Call<QuantityResponse>
}