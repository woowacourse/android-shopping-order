package woowacourse.shopping.data.datasource.order

import java.io.IOException
import woowacourse.shopping.data.network.order.OrderService
import woowacourse.shopping.data.network.order.dto.OrderRequest
import woowacourse.shopping.domain.OrderResult

class OrderRemoteDataSource(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun orders(cartItemIds: List<String>): OrderResult = try {
        val response = orderService.orders(
            request = OrderRequest(cartItemIds = cartItemIds.mapNotNull(String::toLongOrNull)),
        )
        when {
            response.isSuccessful -> OrderResult.Success
            response.code() == 401 -> OrderResult.AuthExpired
            response.code() in 500..599 -> OrderResult.ServerError
            else -> OrderResult.ServerError
        }
    } catch (e: IOException) {
        OrderResult.NetworkError
    }
}
