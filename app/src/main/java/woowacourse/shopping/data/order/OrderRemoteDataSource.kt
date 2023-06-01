package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.SharedPreferencesDb
import woowacourse.shopping.data.order.requestbody.OrderRequestBody
import woowacourse.shopping.presentation.serversetting.ServerSettingPresenter

class OrderRemoteDataSource(private val sharedPreferences: SharedPreferencesDb) : OrderDataSource {
    private val orderClient = ApiClient.client.create(OrderService::class.java)

    private fun getAuthToken() =
        sharedPreferences.getString(ServerSettingPresenter.AUTHORIZATION_TOKEN, "")

    override fun order(onSuccess: () -> Unit, onFailure: () -> Unit) {
        orderClient.order(
            credentials = getAuthToken(),
            OrderRequestBody(
                spendPoint = 0,
                orderItems = listOf()
            )
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
            }
        })
    }
}
