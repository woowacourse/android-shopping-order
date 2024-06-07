package woowacourse.shopping.domain.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.dto.CartItemDto
import woowacourse.shopping.data.model.dto.CartItemsDto
import woowacourse.shopping.data.model.dto.ProductDto
import woowacourse.shopping.data.model.dto.ProductResponseDto
import woowacourse.shopping.data.model.dto.QuantityDto
import woowacourse.shopping.data.model.dto.ShoppingProductDto

interface RetrofitService {
    @GET("/products")
    suspend fun requestProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Response<ProductResponseDto>

    @GET("/products/{id}")
    suspend fun requestProduct(
        @Path("id") id: Long,
    ): Response<ProductDto>

    @GET("/products")
    suspend fun requestProductWithCategory(
        @Query("category") category: String,
        @Query("query") page: Int = 0,
        @Query("size") size: Int = 50,
    ): Response<ProductResponseDto>

    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50,
    ): Response<CartItemDto>

    @GET("/cart-items/counts")
    suspend fun requestCartItemsCount(): Response<QuantityDto>

    @POST("/cart-items")
    suspend fun addCartItem(
        @Body shoppingProductDto: ShoppingProductDto,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun updateCartItemQuantity(
        @Path("id") id: Long,
        @Body quantity: Int,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Long,
    ): Response<Unit>

    @POST("/orders")
    suspend fun makeOrder(
        @Body cartItemIds: CartItemsDto,
    ): Response<Unit>
}
