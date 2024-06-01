package woowacourse.shopping.data.order.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.RetrofitClient.retrofitApi
import woowacourse.shopping.domain.model.DataCallback
import woowacourse.shopping.domain.repository.OrderRepository

object RemoteOrderRepository : OrderRepository {
    override fun createOrder(
        cartItemIds: List<Int>,
        dataCallback: DataCallback<Unit>,
    ) {
        retrofitApi.requestCreateOrder(createOrderRequest = CreateOrderRequest(cartItemIds))
            .enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        if (response.isSuccessful) {
                            dataCallback.onSuccess(Unit)
                        }
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        t: Throwable,
                    ) {
                        dataCallback.onFailure(t)
                    }
                },
            )
    }
}
