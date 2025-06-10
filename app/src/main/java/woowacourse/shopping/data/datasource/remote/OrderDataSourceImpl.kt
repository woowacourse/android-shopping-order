package woowacourse.shopping.data.datasource.remote

import retrofit2.HttpException
import woowacourse.shopping.data.dto.order.CreateOrderRequest
import woowacourse.shopping.data.service.OrderService

class OrderDataSourceImpl(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun createOrder(createOrderRequest: CreateOrderRequest) {
        val response = orderService.createOrder(createOrderRequest)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }
}
