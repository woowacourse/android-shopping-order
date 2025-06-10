package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.order.CreateOrderRequest

interface OrderDataSource {
    suspend fun createOrder(createOrderRequest: CreateOrderRequest)
}
