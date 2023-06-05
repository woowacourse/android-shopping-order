package woowacourse.shopping.server.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.model.data.dto.CartProductDTO
import woowacourse.shopping.model.data.dto.RequestCartDTO

interface CartItemsService {

    @Headers("Authorization: Basic cmluZ2xvQGVtYWlsLmNvbTpyaW5nbG8xMDEwMjM1")
    @GET("/cart-items")
    fun getCartItems(): Call<List<CartProductDTO>>

    @Headers("Authorization: Basic cmluZ2xvQGVtYWlsLmNvbTpyaW5nbG8xMDEwMjM1")
    @POST("/cart-items")
    fun addCartItem(
        @Body requestCartDTO: RequestCartDTO
    ): Call<Unit>

    @Headers("Authorization: Basic cmluZ2xvQGVtYWlsLmNvbTpyaW5nbG8xMDEwMjM1")
    @PATCH("/cart-items/{id}")
    fun patchCartItem(
        @Path("id") id: Long,
        @Body quantity: Int
    ): Call<Unit>

    @Headers("Authorization: Basic cmluZ2xvQGVtYWlsLmNvbTpyaW5nbG8xMDEwMjM1")
    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Long
    ): Call<Unit>
}
