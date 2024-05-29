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
    @GET(ApiClient.Cart.GET_CART_ITEMS)
    fun getCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<CartsResponse>

    @POST(ApiClient.Cart.POST_CART_ITEM)
    fun postCartItem(
        @Body body: PostCartItemRequest,
    ): Call<Unit>

    @DELETE(ApiClient.Cart.DELETE_CART_ITEM)
    fun deleteCartItem(
        @Path("id") id: Int,
    ): Call<Unit>

    @PATCH(ApiClient.Cart.PATCH_CART_ITEMS)
    fun patchCartItem(
        @Path("id") id: Int,
        @Body body: PatchCartItemRequest,
    ): Call<Unit>

    @GET(ApiClient.Cart.GET_CART_ITEMS_COUNT)
    fun getCartItemsCount(): Call<CartItemCountResponse>
}
