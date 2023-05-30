package woowacourse.shopping.data.service.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.dto.CartAddRequest
import woowacourse.shopping.data.dto.CartGetResponse
import woowacourse.shopping.data.dto.CartPatchRequest

typealias ProductId = Int
typealias CartProductId = Int

interface CartService {
    @GET("/cart-items")
    fun getAllCartProduct(): Call<List<CartGetResponse>>

    @POST("/cart-items")
    fun addCartProduct(@Body requestBody: CartAddRequest): Call<Unit>

    @PATCH("/cart-items/{cartItemId}")
    fun updateProductCountById(
        @Path("cartItemId") cartItemId: CartProductId,
        @Body requestBody: CartPatchRequest,
    ): Call<Unit>

    @DELETE("/cart-items/{cartItemId}")
    fun deleteCartProductById(@Path("cartItemId") cartItemId: CartProductId): Call<Unit>
}
