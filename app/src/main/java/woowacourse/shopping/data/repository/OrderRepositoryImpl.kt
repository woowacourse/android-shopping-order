package woowacourse.shopping.data.repository

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.OrderResponse
import woowacourse.shopping.data.dto.mapper.toOrderRequest
import woowacourse.shopping.data.dto.mapper.toOrders
import woowacourse.shopping.data.service.order.OrderService
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(private val service: OrderService) : OrderRepository {

    override fun saveOrder(
        order: Order,
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        service.saveOrder(order.toOrderRequest()).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful && response.body() != null) {
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

    override fun getOrders(
        page: Page,
        onSuccess: (List<Order>) -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        service.getOrders(
            page = page.value,
            size = page.sizePerPage
        ).enqueue(object : Callback<List<OrderResponse>> {
            override fun onResponse(
                call: Call<List<OrderResponse>>,
                response: Response<List<OrderResponse>>,
            ) {
                if (response.isSuccessful && response.body() != null) {
//                    onSuccess(response.body()?.orders?.toOrders() ?: emptyList())
                    onSuccess(response.body()?.toOrders() ?: emptyList())
                    return
                }
                onFailed(Throwable(response.message()))
            }

            override fun onFailure(call: Call<List<OrderResponse>>, throwable: Throwable) {
                Log.d("buna", "$throwable ")
                onFailed(throwable)
            }
        })
    }
}
