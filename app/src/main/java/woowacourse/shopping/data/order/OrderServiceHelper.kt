package woowacourse.shopping.data.order

import retrofit2.Call
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.order.model.OrderAddBody
import woowacourse.shopping.data.order.model.OrderDataModel
import woowacourse.shopping.data.order.model.OrderDetailDataModel

class OrderServiceHelper() : OrderRemoteDataSource {
    private val orderService = ApiClient.client
        .create(OrderService::class.java)

    override fun getAllOrders(): Call<BaseResponse<List<OrderDataModel>>> {
        return orderService.getOrders()
    }

    override fun getOrderDetail(orderId: Int): Call<BaseResponse<OrderDetailDataModel>> {
        return orderService.getOrderDetail(
            orderId = orderId
        )
    }

    override fun addOrder(orderAddBody: OrderAddBody): Call<BaseResponse<Unit>> {
        return orderService.addOrder(
            orderAddBody = orderAddBody
        )
    }
}
