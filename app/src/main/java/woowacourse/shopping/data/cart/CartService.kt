package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.cart.model.AddCartRequestBody
import woowacourse.shopping.data.cart.model.CartDataModel
import woowacourse.shopping.data.cart.model.UpdateQuantityRequestBody
import woowacourse.shopping.data.common.model.BaseResponse

interface CartService {
    @GET("cart-items")
    fun getAllCartItems(
        @Header("Authorization") credentials: String,
    ): Call<BaseResponse<List<CartDataModel>>>

    @Headers("Content-Type: application/json")
    @POST("cart-items")
    fun addCartItem(
        @Header("Authorization") credentials: String,
        @Body addCartRequestBody: AddCartRequestBody,
    ): Call<BaseResponse<Unit>>

    @DELETE("cart-items/{cartItemId}")
    fun deleteCartItem(
        @Header("Authorization") credentials: String,
        @Path("cartItemId") cartItemId: Int,
    ): Call<BaseResponse<CartDataModel>>

    @Headers("Content-Type: application/json")
    @PATCH("cart-items/{cartItemId}")
    fun updateCartItemCount(
        @Header("Authorization") credentials: String,
        @Path("cartItemId") cartItemId: Int,
        @Body quantityBody: UpdateQuantityRequestBody,
    ): Call<BaseResponse<Unit>>
}
