package woowacourse.shopping.network.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.entity.CartItemEntity
import woowacourse.shopping.data.entity.ProductIdEntity
import woowacourse.shopping.data.entity.QuantityEntity

interface CartItemRetrofitService {
    @Headers("Content-Type: application/json")
    @POST("/cart-items")
    fun postCartItem(
        @Header("Authorization") token: String,
        @Body productId: ProductIdEntity
    ): Call<Unit>

    @GET("/cart-items")
    fun selectCartItems(
        @Header("Authorization") token: String
    ): Call<List<CartItemEntity>>

    @PATCH("/cart-items/{id}")
    @Headers("Content-Type: application/json")
    fun updateCountCartItem(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body quantity: QuantityEntity
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Call<Unit>
}
