package woowacourse.shopping.data.dataSource.remote.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.dto.request.AddCartProductDto
import woowacourse.shopping.data.model.dto.request.ChangeCartProductCountDto
import woowacourse.shopping.data.model.dto.response.CartProductDto

interface CartProductService {
    @GET("cart-items")
    fun getAllCartProduct(): Call<List<CartProductDto>>

    @POST("cart-items")
    fun addCartProduct(
        @Body addCartProductDto: AddCartProductDto
    ): Call<Unit>

    @PATCH("cart-items/{cartId}")
    fun changeCartProduct(
        @Path("cartId") cartId: Long,
        @Body changeCartProductCountDto: ChangeCartProductCountDto
    ): Call<Unit>

    @DELETE("cart-items/{cartId}")
    fun deleteCartProduct(
        @Path("cartId") cartId: Long
    ): Call<Unit>
}
