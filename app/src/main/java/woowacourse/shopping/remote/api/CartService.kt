package woowacourse.shopping.remote.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.model.request.PatchCartItemRequest
import woowacourse.shopping.remote.model.request.PostCartItemRequest
import woowacourse.shopping.remote.model.response.CartItemCountResponse
import woowacourse.shopping.remote.model.response.CartsResponse

interface CartService {
    @GET(CART_RELATIVE_URL)
    fun getCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<CartsResponse>

    @POST(CART_RELATIVE_URL)
    fun postCartItem(
        @Body body: PostCartItemRequest,
    ): Call<Unit>

    @DELETE("${CART_RELATIVE_URL}/{id}")
    fun deleteCartItem(
        @Path("id") id: Int,
    ): Call<Unit>

    @PATCH("${CART_RELATIVE_URL}/{id}")
    fun patchCartItem(
        @Path("id") id: Int,
        @Body body: PatchCartItemRequest,
    ): Call<Unit>

    @GET("${CART_RELATIVE_URL}/counts")
    fun getCartItemsCount(): Call<CartItemCountResponse>

    companion object {
        private const val CART_RELATIVE_URL = "/cart-items"
    }
}
