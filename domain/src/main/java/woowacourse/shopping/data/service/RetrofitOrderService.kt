package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.model.OrderList

interface RetrofitOrderService {
    @GET("orders/information")
    fun getOrderList(): Call<OrderList>
}
