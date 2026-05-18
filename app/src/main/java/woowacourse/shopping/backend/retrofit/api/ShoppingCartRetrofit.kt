package woowacourse.shopping.backend.retrofit.api

import retrofit2.Response
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

interface ShoppingCartRetrofit {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
    ): Response<ShoppingCartResponse>

    @POST("/cart-items")
    suspend fun addCartItem(
        @Header("Accept") accept: String = "*/*",
        @Body product: CartRequest,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun updateQuantityCartItem(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Int,
        @Body product: CartQuantity,
    ): Response<Unit>

    // 장바구니 총 수량
    @GET("/cart-items/counts")
    suspend fun requestQuantityCartItem(
        @Header("Accept") accept: String = "*/*",
    ): Response<CartQuantity>
}
