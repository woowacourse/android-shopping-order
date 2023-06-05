package woowacourse.shopping.data.retrofit

import retrofit2.Call
import retrofit2.http.*
import woowacourse.shopping.domain.model.OrderCartItemsDTO
import woowacourse.shopping.domain.model.OrderDTO
import woowacourse.shopping.domain.model.OrdersDTO

interface OrderApi {
    @GET("/orders")
    fun requestOrders(): Call<OrdersDTO>

    @GET("/orders/{id}")
    fun requestOrderDetail(@Path("id") orderId: Int): Call<OrderDTO>

    @POST("/orders")
    fun requestOrderCartItems(@Body orderCartItemDtos: OrderCartItemsDTO): Call<Unit>
}
