package woowacourse.shopping.data.cart.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.cart.model.CartItemResponse

interface CartApiService {
    @GET("/cart-items")
    fun getCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>
    ): Call<CartItemResponse>

    @POST("/cart-items")
    fun postCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("productId") productId: Int,
        @Query("quantity") quantity: Int
    ): String

    @DELETE("/cart-items/{id}")
    fun deleteCartItems(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): String

    @PATCH("/cart-items/{id}")
    fun patchCartItems(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
        @Query("quantity") quantity: Int,
    )

    @PATCH("/cart-items/counts")
    fun getCartItemsCounts(
        @Header("accept") accept: String = "*/*",
        @Query("quantity") quantity: Int,
    )
}