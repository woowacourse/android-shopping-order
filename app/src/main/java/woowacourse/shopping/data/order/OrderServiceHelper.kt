package woowacourse.shopping.data.order

import retrofit2.Call
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.SharedPreferencesDb
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.order.model.OrderAddBody
import woowacourse.shopping.data.order.model.OrderDataModel
import woowacourse.shopping.data.order.model.OrderDetailDataModel
import woowacourse.shopping.presentation.serversetting.ServerSettingPresenter

class OrderServiceHelper(
    private val sharedPreferences: SharedPreferencesDb,
) : OrderRemoteDataSource {
    private val orderService = ApiClient.client
        .create(OrderService::class.java)

    private fun getAuthToken() =
        sharedPreferences.getString(ServerSettingPresenter.AUTHORIZATION_TOKEN, "")

    override fun getAllOrders(): Call<BaseResponse<List<OrderDataModel>>> {
        return orderService.getOrders(
            credentials = getAuthToken()
        )
    }

    override fun getOrderDetail(orderId: Int): Call<BaseResponse<OrderDetailDataModel>> {
        return orderService.getOrderDetail(
            credentials = getAuthToken(),
            orderId = orderId
        )
    }

    override fun addOrder(orderAddBody: OrderAddBody): Call<BaseResponse<Unit>> {
        return orderService.addOrder(
            credentials = getAuthToken(),
            orderAddBody = orderAddBody
        )
    }
}
