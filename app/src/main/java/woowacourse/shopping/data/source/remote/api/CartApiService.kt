package woowacourse.shopping.data.source.remote.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.CartItemResponse

interface CartApiService {
    @GET("/cart-items")
    fun getCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int = 5,
        @Query("sort") sort: List<String> = listOf(""),
    ): Call<CartItemResponse>

    @POST("/cart-items")
    fun postCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("productId") productId: Int,
        @Query("quantity") quantity: Int,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItems(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun patchCartItems(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
        @Query("quantity") quantity: Int,
    ): Call<Unit>

    @PATCH("/cart-items/counts")
    fun getCartItemsCounts(
        @Header("accept") accept: String = "*/*",
    ): Call<CartItemResponse>
}
