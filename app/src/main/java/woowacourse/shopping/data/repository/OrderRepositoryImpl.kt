package woowacourse.shopping.data.repository

import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderMinInfoItem
import com.example.domain.repository.OrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dataSource.remote.order.OrderService
import woowacourse.shopping.data.model.dto.request.OrderRequestDto
import woowacourse.shopping.data.model.dto.response.OrderDetailDto
import woowacourse.shopping.data.model.dto.response.OrderMinInfoItemDto

class OrderRepositoryImpl(
    private val orderService: OrderService
) : OrderRepository {
    override fun getAllOrders(onSuccess: (List<OrderMinInfoItem>) -> Unit, onFailure: () -> Unit) {
        orderService.getOrders().enqueue(object : Callback<List<OrderMinInfoItemDto>> {
            override fun onResponse(
                call: Call<List<OrderMinInfoItemDto>>,
                response: Response<List<OrderMinInfoItemDto>>
            ) {
                if (response.isSuccessful.not()) onFailure()
                onSuccess(response.body()?.map { it.toDomain() } ?: emptyList())
            }

            override fun onFailure(call: Call<List<OrderMinInfoItemDto>>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun getOrderDetailById(
        orderId: Long,
        onSuccess: (OrderDetail) -> Unit,
        onFailure: () -> Unit
    ) {
        orderService.getOrderDetail(orderId).enqueue(object : Callback<OrderDetailDto> {
            override fun onResponse(
                call: Call<OrderDetailDto>,
                response: Response<OrderDetailDto>
            ) {
                if (response.isSuccessful.not()) onFailure()
                response.body()?.let {
                    return onSuccess(it.toDomain())
                }
                return onFailure()
            }

            override fun onFailure(call: Call<OrderDetailDto>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun addOrder(
        cartIds: List<Long>,
        orderPaymentPrice: Int,
        onSuccess: (orderId: Long) -> Unit,
        onFailure: () -> Unit
    ) {
        orderService.addOrder(OrderRequestDto(cartIds, orderPaymentPrice))
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful.not()) onFailure()
                    val orderId = response.headers()["Location"] ?: return onFailure()
                    onSuccess(orderId.toLong())
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
