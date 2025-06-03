package woowacourse.shopping.data.source.remote.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.data.source.remote.api.OrderApiService

class OrderRemoteDataSource(
    private val api: OrderApiService,
) : OrderDataSource {
    override fun orderCheckedItems(
        cartIds: List<Long>,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val request = OrderRequest(cartItemIds = cartIds)
        api.postOrders(request = request).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        onResult(Result.success(Unit))
                    } else {
                        onResult(Result.failure(Exception(POST_ERROR_MESSAGE)))
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            }
        )
    }


    companion object {
        private const val POST_ERROR_MESSAGE = "[ERROR] 주문이 실패했습니다."
    }
}
