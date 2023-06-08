package woowacourse.shopping.data.respository.cart.source.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.CartRemoteEntity

interface CartService {
    @GET(PATH_CART)
    fun requestCartProducts(@Header(AUTHORIZATION) token: String): Call<List<CartRemoteEntity>>

    @Headers(HEADER_JSON)
    @POST(PATH_CART)
    fun requestPostCartProduct(
        @Header(AUTHORIZATION) token: String,
        @Body productId: Long,
    ): Call<Unit>

    @Headers(HEADER_JSON)
    @PATCH("$PATH_CART/{$PATH_CART_ID}")
    fun requestPatchCartProduct(
        @Header(AUTHORIZATION) token: String,
        @Path(PATH_CART_ID) id: Long,
        @Body quantity: Int,
    ): Call<Unit>

    @DELETE("$PATH_CART/{$PATH_CART_ID}")
    fun requestDeleteCartProduct(
        @Header(AUTHORIZATION) token: String,
        @Path(PATH_CART_ID) id: Long,
    ): Call<Unit>

    companion object {
        private const val HEADER_JSON = "Content-Type: application/json"
        private const val PATH_CART = "/cart-items"
        private const val PATH_CART_ID = "cartItemId"
        private const val AUTHORIZATION = "Authorization"
    }
}
