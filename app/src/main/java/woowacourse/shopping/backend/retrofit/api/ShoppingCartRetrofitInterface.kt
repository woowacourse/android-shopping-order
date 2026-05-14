package woowacourse.shopping.backend.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.backend.retrofit.dto.CartQuantity
import woowacourse.shopping.backend.retrofit.dto.CartRequest
import woowacourse.shopping.backend.retrofit.dto.ShoppingCartResponse

interface ShoppingCartRetrofitInterface {
    @GET("/cart-items")
    fun requestCartItems(
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>,
    ): Call<ShoppingCartResponse>

    @POST("/cart-items")
    fun addCartItem(
        @Header("Accept") accept: String = "*/*",
        @Body product: CartRequest,
    ): Call<Void>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Call<Void>

    @PATCH("/cart-items/{id}")
    fun updateQuantityCartItem(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Int,
        @Body product: CartQuantity,
    ): Call<Void>

    // 장바구니 총 수량
    @GET("/cart-items/counts")
    fun requestQuantityCartItem(
        @Header("Accept") accept: String = "*/*",
    ): Call<CartQuantity>
}
