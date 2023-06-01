package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.CartProductDto
import woowacourse.shopping.data.model.PointDto
import woowacourse.shopping.data.model.ProductDto

interface RetrofitService {
    @GET("/products")
    fun requestProducts(): Call<List<ProductDto>>

    @GET("/cart-items")
    fun requestCartProducts(
        @Header("Authorization") credential: String,
    ): Call<List<CartProductDto>>

    @Headers("Content-Type: application/json")
    @POST("/cart-items")
    fun addCartProduct(
        @Header("Authorization") credential: String,
        @Body productId: Int,
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @PATCH("/cart-items/{cartItemId}")
    fun updateCartProduct(
        @Header("Authorization") credential: String,
        @Path("cartItemId") cartItemId: Int,
        @Body quantity: Int,
    ): Call<Void>

    @DELETE("/cart-items/{cartItemId}")
    fun deleteCartProduct(
        @Header("Authorization") credential: String,
        @Path("cartItemId") cartItemId: Int,
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @GET("/points")
    fun requestPoints(
        @Header("Authorization") credential: String
    ): Call<PointDto>
}
