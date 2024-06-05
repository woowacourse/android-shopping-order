package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.cart.remote.AddCartItemRequest
import woowacourse.shopping.data.cart.remote.CartItemQuantityRequest
import woowacourse.shopping.data.cart.remote.CartResponse
import woowacourse.shopping.data.cart.remote.CountResponse
import woowacourse.shopping.data.order.remote.CreateOrderRequest
import woowacourse.shopping.data.product.remote.Content
import woowacourse.shopping.data.product.remote.ProductResponse

interface RetrofitService {
    @GET("/products")
    fun requestProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun requestProduct(
        @Path("id") id: Int = 0,
    ): Call<Content>

    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Call<CartResponse>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Int = 0,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun setCartItemQuantity(
        @Path("id") id: Int = 0,
        @Body quantity: CartItemQuantityRequest,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun requestCartQuantityCount(): Call<CountResponse>

    @POST("/cart-items")
    fun requestCartQuantityCount(
        @Body addCartItemRequest: AddCartItemRequest,
    ): Call<Unit>

    @POST("/orders")
    fun requestCreateOrder(
        @Body createOrderRequest: CreateOrderRequest,
    ): Call<Unit>
}
