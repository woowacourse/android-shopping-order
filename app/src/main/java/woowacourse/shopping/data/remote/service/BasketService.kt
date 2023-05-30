package woowacourse.shopping.data.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.DataBasketProduct
import woowacourse.shopping.data.remote.request.BasketAddRequest
import woowacourse.shopping.data.remote.request.BasketUpdateRequest

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
}
