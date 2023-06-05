package woowacourse.shopping.data.repository

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.OrdersResponse
import woowacourse.shopping.data.dto.mapper.toOrderRequest
import woowacourse.shopping.data.dto.mapper.toOrders
import woowacourse.shopping.data.service.order.OrderService
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.OrderRepository

class DefaultOrderRepository(private val service: OrderService) : OrderRepository {

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
        ).enqueue(object : Callback<OrdersResponse> {
            override fun onResponse(
                call: Call<OrdersResponse>,
                response: Response<OrdersResponse>,
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()?.orders?.toOrders() ?: emptyList())
                    return
                }
                onFailed(Throwable(response.message()))
            }

            override fun onFailure(call: Call<OrdersResponse>, throwable: Throwable) {
                Log.d("buna", "$throwable ")
                onFailed(throwable)
            }
        })
    }
}
