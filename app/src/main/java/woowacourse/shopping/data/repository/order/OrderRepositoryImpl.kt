package woowacourse.shopping.data.repository.order

import com.example.domain.model.Order
import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderProduct
import com.example.domain.repository.OrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.order.OrderService
import woowacourse.shopping.data.model.OrderDetailDto
import woowacourse.shopping.data.model.OrderListDto
import woowacourse.shopping.data.model.OrderRequestDto
import woowacourse.shopping.data.model.toData
import woowacourse.shopping.data.model.toDomain

class OrderRepositoryImpl(
    private val service: OrderService
) : OrderRepository {
    override fun getOrders(page: Int, onSuccess: (List<Order>) -> Unit, onFailure: () -> Unit) {
        service.getAll(page).enqueue(object : Callback<OrderListDto> {
            override fun onResponse(call: Call<OrderListDto>, response: Response<OrderListDto>) {
                response.body()?.let { orderListDto ->
                    onSuccess(orderListDto.contents.map { it.toDomain() })
                }
            }

            override fun onFailure(call: Call<OrderListDto>, t: Throwable) {
                onFailure()
            }

        })
    }

    override fun placeOrder(
        usedPoint: Int,
        orderProducts: List<OrderProduct>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val orderRequest = OrderRequestDto(usedPoint, orderProducts.map { it.toData() })

        service.placeOrder(orderRequest).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun getOrderDetail(
        orderId: Int,
        onSuccess: (OrderDetail) -> Unit,
        onFailure: () -> Unit
    ) {
        service.getOrderDetail(orderId).enqueue(object : Callback<OrderDetailDto> {
            override fun onResponse(
                call: Call<OrderDetailDto>,
                response: Response<OrderDetailDto>
            ) {
                response.body()?.let {
                    onSuccess(it.toDomain())
                }
            }

            override fun onFailure(call: Call<OrderDetailDto>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun cancelOrder(orderId: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        service.deleteOrder(orderId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
            }

        })
    }
}
