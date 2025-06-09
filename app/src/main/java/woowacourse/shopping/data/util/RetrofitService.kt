package woowacourse.shopping.data.util

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.carts.dto.CartItemRequest
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.coupons.CouponRequest
import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.data.goods.dto.GoodsResponse

interface RetrofitService {
    @GET("/products")
    suspend fun requestProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("category") category: String? = null,
    ): GoodsResponse

    @GET("/products/{id}")
    suspend fun requestProductDetail(
        @Path("id") id: Long = 0,
    ): Content

    @GET("/cart-items")
    suspend fun requestCartProduct(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
        @Query("sort") sort: String = "id,desc",
        @Header("Authorization") authorization: String,
    ): CartResponse

    @GET("/cart-items/counts")
    suspend fun requestCartCounts(
        @Header("Authorization") authorization: String,
    ): CartQuantity

    @PATCH("/cart-items/{id}")
    suspend fun updateCartCounts(
        @Path("id") cartId: Int,
        @Body requestBody: CartQuantity,
        @Header("Authorization") authorization: String,
    )

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") cartId: Int,
        @Header("Authorization") authorization: String,
    )

    @POST("/cart-items")
    suspend fun addCartItem(
        @Body cartItem: CartItemRequest,
        @Header("Authorization") authorization: String,
    )

    @GET("/coupons")
    suspend fun requestCoupons(
    ): CouponRequest

    @POST("/orders")
    suspend fun addOrder(
        @Body cartItemIds : List<Int>
    )
}

class AppInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithHeaders = chain.request().newBuilder()
            .addHeader("accept", "*/*")
            .build()
        return chain.proceed(requestWithHeaders)
    }
}