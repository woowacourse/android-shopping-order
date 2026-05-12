package woowacourse.shopping.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.retrofit.dto.CartQuantity
import woowacourse.shopping.retrofit.dto.CartRequest
import woowacourse.shopping.retrofit.dto.OrderInfo
import woowacourse.shopping.retrofit.dto.Product
import woowacourse.shopping.retrofit.dto.ProductResponse
import woowacourse.shopping.retrofit.dto.ShoppingCartResponse

interface RetrofitInterface {

    /**
     * 상품관련API
     */
    @GET("products")
    fun requestProducts(
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>,
        @Query("category") category: String? = null,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun requestProductDetail(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Call<Product>

    @POST("/products")
    fun addProduct(
        @Header("Accept") accept: String = "*/*",
        @Body product: Product
    ): Call<Void>

    @DELETE("/products/{id}")
    fun deleteProduct(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Call<Void>

    /**
     * 주문
     */

    @POST("/orders")
    fun order(
        @Header("Accept") accept: String = "*/*",
        @Body order: OrderInfo
    ): Call<Void>

    /**
     * 장바구니
     */

    @GET("/cart-items")
    fun getCartItems(
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>,
    ): Call<ShoppingCartResponse>

    @POST("/cart-items")
    fun addCartItem(
        @Header("Accept") accept: String = "*/*",
        @Body product: CartRequest
    ): Call<Void>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Call<Void>

    @POST("/cart-items/{id}")
    fun updateQuantityCartItem(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Int,
        @Body product: CartQuantity
    ): Call<Void>

    @GET("/cart-items/counts")
    fun getQuantityCartItem(
        @Header("Accept") accept: String = "*/*",
    ): Call<CartQuantity>
}


