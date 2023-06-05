package woowacourse.shopping.data.httpclient.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.httpclient.request.BasketAddRequest
import woowacourse.shopping.data.httpclient.request.BasketUpdateRequest
import woowacourse.shopping.data.model.DataBasketProduct

interface BasketService {
    @GET("/cart-items")
    fun getAll(): Call<List<DataBasketProduct>>

    @POST("/cart-items")
    fun add(
        @Body body: BasketAddRequest
    ): Call<Unit>

    @PATCH("/cart-items/{cartItemId}")
    fun update(
        @Path("cartItemId") cartItemId: Int,
        @Body body: BasketUpdateRequest
    ): Call<Unit>

    @DELETE("/cart-items/{cartItemId}")
    fun delete(
        @Path("cartItemId") cartItemId: Int
    ): Call<Unit>
}
