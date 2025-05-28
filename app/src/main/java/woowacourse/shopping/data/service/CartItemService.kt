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
import woowacourse.shopping.data.dto.cartitem.ProductResponse
import woowacourse.shopping.data.dto.cartitem.Quantity

interface CartItemService {
    @GET("/cart-items")
    fun requestCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1,
        @Query("sort") sort: List<String> = listOf(),
    ): Call<ProductResponse>

    @POST("/cart-items")
    fun postCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("productId") productId: Int,
        @Query("quantity") quantity: Int = 1,
    ): Call<Void>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") productId: Int,
    ): Call<Void>

    @PATCH("/cart-items/{id}")
    fun patchCartItemQuantity(
        @Header("accept") accept: String = "*/*",
        @Path("id") cartItemId: Int,
        @Body quantity: Quantity,
    ): Call<Quantity>

    @PATCH("/cart-items/counts")
    fun getCartItemsCount(
        @Header("accept") accept: String = "*/*",
    ): Call<Quantity>
}
