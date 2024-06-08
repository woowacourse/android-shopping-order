package woowacourse.shopping.data.remote.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.RetrofitModule
import woowacourse.shopping.data.remote.dto.request.CartItemRequestDto
import woowacourse.shopping.data.remote.dto.request.QuantityRequestDto
import woowacourse.shopping.data.remote.dto.response.CartResponseDto
import woowacourse.shopping.data.remote.dto.response.QuantityResponseDto

interface CartItemApi {
    @GET("/cart-items")
    suspend fun getCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Response<CartResponseDto>

    @POST("/cart-items")
    suspend fun postCartItem(
        @Header("accept") accept: String = "*/*",
        @Body cartItemRequestDto: CartItemRequestDto,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
        @Body quantityRequestDto: QuantityRequestDto,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun getCartItemsCounts(
        @Header("accept") accept: String = "*/*",
    ): Response<QuantityResponseDto>

    companion object {
        private var service: CartItemApi? = null
        fun service(): CartItemApi {
            return service ?: RetrofitModule.defaultBuild.create(CartItemApi::class.java)
        }
    }
}
