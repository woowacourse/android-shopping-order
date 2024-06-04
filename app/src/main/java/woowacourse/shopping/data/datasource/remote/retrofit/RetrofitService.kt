package woowacourse.shopping.data.datasource.remote.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.datasource.remote.model.request.AddCartItemRequest
import woowacourse.shopping.data.datasource.remote.model.request.CartItemQuantityRequest
import woowacourse.shopping.data.datasource.remote.model.request.CreateOrderRequest
import woowacourse.shopping.data.datasource.remote.model.response.cart.CartResponse
import woowacourse.shopping.data.datasource.remote.model.response.count.CountResponse
import woowacourse.shopping.data.datasource.remote.model.response.product.ProductContent
import woowacourse.shopping.data.datasource.remote.model.response.product.ProductResponse

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
    ): Call<ProductContent>

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
        @Body quantity: CartItemQuantityRequest,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun requestCartQuantityCount(
        @Header("accept") accept: String = "*/*",
    ): Call<CountResponse>

    @POST("/cart-items")
    fun requestCartQuantityCount(
        @Header("accept") accept: String = "*/*",
        @Body addCartItemRequest: AddCartItemRequest,
    ): Call<Unit>

    @POST("/orders")
    fun requestCreateOrder(
        @Header("accept") accept: String = "*/*",
        @Body createOrderRequest: CreateOrderRequest,
    ): Call<Unit>
}
