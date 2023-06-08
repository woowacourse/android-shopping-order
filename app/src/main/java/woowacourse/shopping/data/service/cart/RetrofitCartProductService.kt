package woowacourse.shopping.data.service.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.dto.CartProductDto

interface RetrofitCartProductService {
    @GET("/cart-items")
    fun requestCartProducts(): Call<List<CartProductDto>>

    @POST("/cart-items")
    fun addCartProductByProductId(
        @Body productId: String,
    ): Call<Int>

    @PATCH("/cart-items/{cartItemId}")
    fun updateCartProductCountById(
        @Path("cartItemId") cartItemId: String,
        @Body quantity: Int,
    ): Call<Void>

    @DELETE("/cart-items/{cartItemId}")
    fun deleteCartProductById(
        @Path("cartItemId") cartItemId: String,
    ): Call<CartProductDto>

    @GET("/cart-items/products/{productId}")
    fun requestCartProductByProductId(
        @Path("productId") productId: String,
    ): Call<CartProductDto>
}
