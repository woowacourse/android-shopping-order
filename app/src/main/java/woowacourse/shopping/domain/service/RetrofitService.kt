package woowacourse.shopping.domain.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.dto.ProductDto
import woowacourse.shopping.data.model.dto.ProductResponseDto

interface RetrofitService {
    @GET("/products")
    fun requestProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductResponseDto>

    @GET("/products/{id}")
    fun requestProduct(
        @Path("id") id: Long,
    ): Call<ProductDto>

    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductResponseDto>

    @POST("/cart-items")
    fun addCartItem(
        @Body productId: Long,
        @Body quantity: Long,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun updateCartItemQuantity(
        @Path("id") id: Long,
        @Body quantity: Long,
    ): Call<Unit>
}
