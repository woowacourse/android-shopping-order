package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import woowacourse.shopping.model.OrderList

interface RetrofitOrderService {
    @GET("cart-items/checkout")
    fun getOrderList(
        @Header("Authorization") token: String,
        @Query("ids") cartItemIds: String
    ): Call<OrderList>
}
