package woowacourse.shopping.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.dto.request.CartSaveRequest
import woowacourse.shopping.remote.dto.request.CartUpdateRequest
import woowacourse.shopping.remote.dto.response.CartQuantityResponse
import woowacourse.shopping.remote.dto.response.CartResponse

interface CartApiService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<CartResponse>

    @POST("/cart-items")
    suspend fun requestAddCartItems(
        @Body cartRequest: CartSaveRequest,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun requestUpdateCartItems(
        @Path(value = "id") cartId: Int,
        @Body request: CartUpdateRequest,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun requestDeleteCartItems(
        @Path(value = "id") cartId: Int,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun requestCartItemsCount(): Response<CartQuantityResponse>
}
