package woowacourse.shopping.data.retrofit

import retrofit2.Call
import retrofit2.http.*
import woowacourse.shopping.domain.model.OrderCartItemsDTO
import woowacourse.shopping.domain.model.OrderDTO
import woowacourse.shopping.domain.model.OrdersDTO

interface OrderApi {
    @Headers("Authorization: Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ=")
    @GET("/orders")
    fun requestOrders(): Call<OrdersDTO>

    @Headers("Authorization: Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ=")
    @GET("/orders/{id}")
    fun requestOrderDetail(@Path("id") orderId: Int): Call<OrderDTO>

    @Headers("Authorization: Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ=")
    @POST("/orders")
    fun requestOrderCartItems(@Body orderCartItemDtos: OrderCartItemsDTO): Call<Unit>
}
