package woowacourse.shopping.data.util

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.carts.dto.CartItemRequest
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.data.goods.dto.GoodsResponse

interface RetrofitService {
    @GET("/products")
    fun requestProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("category") category: String? = null,
    ): Call<GoodsResponse>

    @GET("/products/{id}")
    fun requestProductDetail(
        @Path("id") id: Long = 0,
    ): Call<Content>

    @GET("/cart-items")
    suspend fun requestCartProduct(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
        @Query("sort") sort: String = "id,desc",
    ): CartResponse

    @GET("/cart-items/counts")
    fun requestCartCounts(): Call<CartQuantity>

    @PATCH("/cart-items/{id}")
    suspend fun updateCartCounts(
        @Path("id") cartId: Int,
        @Body requestBody: CartQuantity,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") cartId: Int,
    ): Response<Unit>

    @POST("/cart-items")
    fun addCartItem(
        @Body cartItem: CartItemRequest,
    ): Call<Unit>
}
