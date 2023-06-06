package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.common.BaseResponse

interface CartService {
    @GET("cart-items")
    fun getAllCartItems(): Call<BaseResponse<List<CartDataModel>>>

    @POST("cart-items")
    fun addCartItem(
        @Body addCartRequestBody: AddCartRequestBody,
    ): Call<Unit>

    @DELETE("cart-items/{cartItemId}")
    fun deleteCartItem(
        @Path("cartItemId") cartItemId: Int,
    ): Call<Unit>

    @PATCH("cart-items/{cartItemId}")
    fun updateCartItemCount(
        @Path("cartItemId") cartItemId: Int,
        @Body quantityBody: UpdateQuantityRequestBody,
    ): Call<Unit>
}
