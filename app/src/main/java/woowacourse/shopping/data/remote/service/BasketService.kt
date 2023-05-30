package woowacourse.shopping.data.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import woowacourse.shopping.data.model.DataBasketProduct
import woowacourse.shopping.data.remote.request.BasketAddRequest

interface BasketService {
    @GET("/cart-items")
    fun getAll(): Call<List<DataBasketProduct>>

    @POST("/cart-items")
    fun add(
        @Body body: BasketAddRequest
    ): Call<Unit>
}
