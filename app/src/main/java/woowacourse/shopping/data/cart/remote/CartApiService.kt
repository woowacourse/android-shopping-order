package woowacourse.shopping.data.cart.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.request.AddCartRequestBody
import woowacourse.shopping.data.dto.request.UpdateCartRequestBody
import woowacourse.shopping.data.dto.response.CartQuantityResponse
import woowacourse.shopping.data.dto.response.CartResponse

interface CartApiService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): CartResponse

    @POST("/cart-items")
    suspend fun requestAddCartItems(
        @Body addCartRequestBody: AddCartRequestBody,
    )

    @PATCH("/cart-items/{id}")
    suspend fun requestUpdateCartItems(
        @Path(value = "id") cartId: Long,
        @Body updateCartRequestBody: UpdateCartRequestBody,
    )

    @DELETE("/cart-items/{id}")
    suspend fun requestDeleteCartItems(
        @Path(value = "id") cartId: Long,
    )

    @GET("/cart-items/counts")
    suspend fun requestGetTotalCartItemCount(): CartQuantityResponse
}
