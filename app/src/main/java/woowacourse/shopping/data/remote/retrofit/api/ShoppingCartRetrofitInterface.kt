package woowacourse.shopping.data.remote.retrofit.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
    ): ShoppingCartResponse

    @POST("/cart-items")
    suspend fun addCartItem(
        @Body product: CartRequest,
    )

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Int,
    )

    @PATCH("/cart-items/{id}")
    suspend fun updateQuantityCartItem(
        @Path("id") id: Int,
        @Body product: CartQuantity,
    )
}
