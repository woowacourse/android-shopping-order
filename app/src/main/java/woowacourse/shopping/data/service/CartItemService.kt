package woowacourse.shopping.data.service

import retrofit2.Call
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
    ): ResponseCartItemsGetDto

    @POST("/cart-items")
    suspend fun postCartItem(
        @Body request: RequestCartItemPostDto,
    )

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Long,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItem(
        @Path("id") id: Long,
        @Body request: RequestCartItemsPatchDto,
    )

    @GET("/cart-items/counts")
    suspend fun getCartItemCounts(): ResponseCartItemCountsGetDto
}
