package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.cart.remote.CartResponse
import woowacourse.shopping.data.product.remote.retrofit.Content
import woowacourse.shopping.data.product.remote.retrofit.ProductResponse

interface RetrofitService {
    @GET("/products")
    fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun requestProduct(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int = 0,
    ): Call<Content>

    @GET("/cart-items")
    fun requestCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Call<CartResponse>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int = 0,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun setCartItemQuantity(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int = 0,
        @Field("quantity") quantity: Int = 1,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun requestCartQuantityCount(
        @Header("accept") accept: String = "*/*",
    ): Call<Int>
}
