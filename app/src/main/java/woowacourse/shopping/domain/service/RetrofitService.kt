package woowacourse.shopping.domain.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.dto.CartItemDto
import woowacourse.shopping.data.model.dto.ProductDto
import woowacourse.shopping.data.model.dto.ProductResponseDto
import woowacourse.shopping.data.model.dto.QuantityDto
import woowacourse.shopping.data.model.dto.ShoppingProductDto

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

    @GET("/products")
    fun requestProductWithCategory(
        @Query("category") category: String,
        @Query("query") query: Int = 0,
        @Query("size") size: Int = 50,
    )

    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Call<CartItemDto>

    @GET("/cart-items/counts")
    fun requestCartItemsCount(): Call<QuantityDto>

    @POST("/cart-items")
    fun addCartItem(
        @Body shoppingProductDto: ShoppingProductDto,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun updateCartItemQuantity(
        @Path("id") id: Long,
        @Body quantity: Int,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Long,
    ): Call<Unit>
}
