package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.api.OrderApi
import woowacourse.shopping.data.model.request.OrderProductsRequest
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepository(
    private val api: OrderApi,
) : OrderRepository {
    override fun postOrderProducts(
        cartIds: List<Long>,
        callback: (Result<Unit>) -> Unit,
    ) {
        api.postOrderProducts(OrderProductsRequest(cartIds)).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    callback(Result.success(Unit))
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }
}
