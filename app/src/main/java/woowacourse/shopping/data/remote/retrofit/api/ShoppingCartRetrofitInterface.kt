package woowacourse.shopping.data.remote.retrofit.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.retrofit.dto.CartQuantity
import woowacourse.shopping.data.remote.retrofit.dto.CartRequest
import woowacourse.shopping.data.remote.retrofit.dto.ShoppingCartResponse

interface ShoppingCartRetrofitInterface {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
    ): ShoppingCartResponse

    @POST("/cart-items")
    suspend fun addCartItem(
        @Header("Accept") accept: String = "*/*",
        @Body product: CartRequest,
    ): Unit

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Unit

    @PATCH("/cart-items/{id}")
    suspend fun updateQuantityCartItem(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Int,
        @Body product: CartQuantity,
    ): Unit
}
