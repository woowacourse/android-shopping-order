package woowacourse.shopping.data.order

import retrofit2.Call
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.order.model.OrderAddBody
import woowacourse.shopping.data.order.model.OrderDataModel
import woowacourse.shopping.data.order.model.OrderDetailDataModel

interface OrderRemoteDataSource {
    fun getAllOrders(): Call<BaseResponse<List<OrderDataModel>>>

    fun getOrderDetail(orderId: Int): Call<BaseResponse<OrderDetailDataModel>>
    fun addOrder(orderAddBody: OrderAddBody): Call<BaseResponse<Unit>>
}
