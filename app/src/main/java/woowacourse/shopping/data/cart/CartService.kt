package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.cart.request.PatchCartProductQuantityRequest
import woowacourse.shopping.data.cart.request.PostCartProductRequest
import woowacourse.shopping.data.cart.response.GetCartProductResponse

interface CartService {
    @GET("cart-items")
    fun requestCartProducts(): Call<List<GetCartProductResponse>>

    @POST("cart-items")
    fun createCartProduct(
        @Body body: PostCartProductRequest
    ) : Call<Unit>

    @PATCH("cart-items/{id}")
    fun updateCartProductQuantity(
        @Path("id") id: Int,
        @Body body: PatchCartProductQuantityRequest
    ) : Call<Unit>

    @DELETE("cart-items/{id}")
    fun deleteCartProduct(
        @Path("id") id: Int
    ) : Call<Unit>
}