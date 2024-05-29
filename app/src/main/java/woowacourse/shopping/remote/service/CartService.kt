package woowacourse.shopping.remote.service


import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import woowacourse.shopping.remote.dto.request.CartItemRequest
import woowacourse.shopping.remote.dto.response.ProductsResponse

interface CartService {
    @GET("cart-items")
    fun fetchCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ProductsResponse>

    @GET("cart-items/counts")
    fun fetchCartItemsCount(@Path("id") id: Long): Call<ProductsResponse>

    @POST("cart-items")
    fun createCartItems(@Body body: CartItemRequest): Call<Unit>

    @DELETE("cart-items/{id}")
    fun deleteCartItem(@Path("id") id: Long): Call<ProductsResponse>

    @PATCH("cart-items/{id}")
    fun patchCartItem(@Path("id") id: Long): Call<ProductsResponse>
}

