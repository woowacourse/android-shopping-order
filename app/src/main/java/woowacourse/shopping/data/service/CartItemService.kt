package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto

interface CartItemService {
    @GET("/cart-items")
    suspend fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ResponseCartItemsGetDto>

    @POST("/cart-items")
    suspend fun postCartItem(
        @Body request: RequestCartItemPostDto,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Long,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItem(
        @Path("id") id: Long,
        @Body request: RequestCartItemsPatchDto,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun getCartItemCounts(): Response<ResponseCartItemCountsGetDto>
}
