package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.request.CartProductQuantityRequestDto
import woowacourse.shopping.data.dto.request.CartProductRequestDto
import woowacourse.shopping.data.dto.response.CartProductQuantityResponseDto
import woowacourse.shopping.data.dto.response.CartProductResponseDto

interface CartProductApiService {
    @GET("/cart-items")
    suspend fun getPagedProducts(
        @Query("page") page: Int?,
        @Query("size") size: Int?,
    ): Response<CartProductResponseDto>

    @POST("/cart-items")
    suspend fun insert(
        @Body body: CartProductRequestDto,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun delete(
        @Path("id") id: Int,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun getTotalQuantity(): Response<CartProductQuantityResponseDto>

    @PATCH("/cart-items/{id}")
    suspend fun updateQuantity(
        @Path("id") id: Int,
        @Body body: CartProductQuantityRequestDto,
    ): Response<Unit>
}
