package woowacourse.shopping.data.datasource.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.remote.request.OrderDTO

interface OrderHistoryService {

    @GET("orders")
    fun getOrderList(
        @Header("Authorization") token: String,
    ): Call<List<OrderDTO>>
}
