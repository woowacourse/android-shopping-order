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
    suspend fun requestProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Result<ProductResponse>

    @GET("/products/{id}")
    suspend fun requestProduct(
        @Path("id") id: Int = 0,
    ): Result<Content>

    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Result<CartResponse>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Int = 0,
    ): Result<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun setCartItemQuantity(
        @Path("id") id: Int = 0,
        @Body quantity: CartItemQuantityRequest,
    ): Result<Unit>

    @GET("/cart-items/counts")
    suspend fun requestCartQuantityCount(): Result<CountResponse>

    @POST("/cart-items")
    suspend fun requestCartQuantityCount(
        @Body addCartItemRequest: AddCartItemRequest,
    ): Result<Unit>

    @POST("/orders")
    fun requestCreateOrder(
        @Body createOrderRequest: CreateOrderRequest,
    ): Call<Unit>
}
