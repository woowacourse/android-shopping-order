package woowacourse.shopping.data.remote.service

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.model.DataBasketProduct

interface BasketService {
    @GET("/cart-items")
    fun getAllBasketProducts(): Call<List<DataBasketProduct>>
}
