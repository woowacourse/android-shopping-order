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
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto

interface CartItemService {
    @GET("/cart-items")
    fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ResponseCartItemsGetDto>

    @POST("/cart-items")
    fun postCartItem(
        @Body request: RequestCartItemPostDto,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Long,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun patchCartItem(
        @Path("id") id: Long,
        @Body request: RequestCartItemsPatchDto,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartItemCounts(): Call<ResponseCartItemCountsGetDto>
}
