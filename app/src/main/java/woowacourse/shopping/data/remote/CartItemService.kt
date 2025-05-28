package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import woowacourse.shopping.data.dto.cart.CartsResponse
import woowacourse.shopping.data.dto.product.ProductsResponse

interface CartItemService {
    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Call<CartsResponse>

    @POST("/cart-items")
    fun addCartItem(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductsResponse>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductsResponse>

    @PATCH("/cart-items/{id}")
    fun updateCartItem(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductsResponse>

    @GET("/cart-items/counts")
    fun requestCartItemCount(): Call<CartItemCountResponse>
}
