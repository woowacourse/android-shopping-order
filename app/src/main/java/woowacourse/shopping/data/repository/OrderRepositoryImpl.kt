package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.mapper.toOrderRequest
import woowacourse.shopping.data.service.order.OrderService
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(private val service: OrderService) : OrderRepository {
    override fun order(
        order: Order,
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        service.order(order.toOrderRequest()).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                    return
                }
                onFailed(Throwable(response.message()))
            }

            override fun onFailure(call: Call<Unit>, throwable: Throwable) {
                onFailed(throwable)
            }
        })
    }
}
