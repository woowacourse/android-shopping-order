package woowacourse.shopping.data.respository.cart.source.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.CartRemoteEntity

interface CartService {
    @GET(PATH_CART)
    fun requestCartProducts(): Call<List<CartRemoteEntity>>

    @POST(PATH_CART)
    fun requestPostCartProduct(
        @Body productId: Long,
    ): Call<Unit>

    @PATCH("$PATH_CART/{$PATH_CART_ID}")
    fun requestPatchCartProduct(
        @Path(PATH_CART_ID) id: Long,
        @Body quantity: Int,
    ): Call<Unit>

    @DELETE("$PATH_CART/{$PATH_CART_ID}")
    fun requestDeleteCartProduct(
        @Path(PATH_CART_ID) id: Long,
    ): Call<Unit>

    companion object {
        private const val HEADER_JSON = "Content-Type: application/json"
        private const val PATH_CART = "/cart-items"
        private const val PATH_CART_ID = "cartItemId"
    }
}
