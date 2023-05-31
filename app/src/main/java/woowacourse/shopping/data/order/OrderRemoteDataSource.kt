package woowacourse.shopping.data.order

import retrofit2.Call
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.order.model.OrderDataModel
import woowacourse.shopping.data.order.model.OrderDetailDataModel
import woowacourse.shopping.data.order.model.OrderItemBody

interface OrderRemoteDataSource {
    fun getAllOrders(): Call<BaseResponse<List<OrderDataModel>>>

    fun getOrderDetail(orderId: Int): Call<BaseResponse<OrderDetailDataModel>>

    fun addOrder(orderItemBodyList: List<OrderItemBody>): Call<BaseResponse<Unit>>
}
