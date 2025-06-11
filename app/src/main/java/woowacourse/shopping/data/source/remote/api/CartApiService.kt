package woowacourse.shopping.data.source.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.CartItemResponse
import woowacourse.shopping.data.model.CartRequest
import woowacourse.shopping.data.model.ItemCount

interface CartApiService {
    @GET("/cart-items")
    suspend fun getCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("sort") sort: List<String> = listOf(""),
    ): Response<CartItemResponse>

    @POST("/cart-items")
    suspend fun postCartItems(
        @Header("accept") accept: String = "*/*",
        @Body request: CartRequest,
    ): Response<Void>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItems(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Response<Void>

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItems(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
        @Body quantity: Int,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun getCartItemsCounts(
        @Header("accept") accept: String = "*/*",
    ): Response<ItemCount>
}
