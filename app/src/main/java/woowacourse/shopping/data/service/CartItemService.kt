package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.dto.response.ResponseCartItemGetDto

interface CartItemService {

    @GET("/cart-items")
    fun getCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ResponseCartItemGetDto>

    @POST("/cart-items")
    fun postCartItem(
        @Header("accept") accept: String = "*/*",
        @Body request: RequestCartItemPostDto
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun patchCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
        @Body request: RequestCartItemsPatchDto
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartItemCounts(
        @Header("accept") accept: String = "*/*",
    ): Call<ResponseCartItemCountsGetDto>

}
