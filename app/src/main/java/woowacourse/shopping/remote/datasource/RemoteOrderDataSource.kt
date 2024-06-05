package woowacourse.shopping.remote.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.data.order.OrderDataSource
import woowacourse.shopping.remote.api.ApiClient
import woowacourse.shopping.remote.api.OrderApiService
import woowacourse.shopping.remote.dto.request.OrderRequest

class RemoteOrderDataSource(
    private val orderApiService: OrderApiService =
        ApiClient.getApiClient().create(OrderApiService::class.java),
) : OrderDataSource {
    override fun requestOrder(
        cartItemIds: List<Int>,
        callBack: (NetworkResult<Unit>) -> Unit,
    ) {
        orderApiService.requestOrder(OrderRequest(cartItemIds)).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    callBack(NetworkResult.Success(Unit))
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callBack(NetworkResult.Error)
                }
            },
        )
    }
}
