package woowacourse.shopping.remote.api

import retrofit2.Response
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
    suspend fun getCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): CartsResponse

    @POST(ApiClient.Cart.POST_CART_ITEM)
    suspend fun postCartItem(
        @Body body: PostCartItemRequest,
    ): Response<Unit>

    @DELETE(ApiClient.Cart.DELETE_CART_ITEM)
    suspend fun deleteCartItem(
        @Path("id") id: Int,
    )

    @PATCH(ApiClient.Cart.PATCH_CART_ITEMS)
    suspend fun patchCartItem(
        @Path("id") id: Int,
        @Body body: PatchCartItemRequest,
    )

    @GET(ApiClient.Cart.GET_CART_ITEMS_COUNT)
    suspend fun getCartItemsCount(): CartItemCountResponse
}
