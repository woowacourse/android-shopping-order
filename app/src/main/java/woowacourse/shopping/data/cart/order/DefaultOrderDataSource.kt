package woowacourse.shopping.data.cart.order

import woowacourse.shopping.data.util.executeAsResult
import woowacourse.shopping.remote.dto.request.OrderRequest
import woowacourse.shopping.remote.service.OrderService
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService

class DefaultOrderDataSource(
    private val ioExecutor: ExecutorService,
    private val orderService: OrderService,
) : OrderDataSource {
    override fun orderProducts(productIds: List<Long>): Result<Unit> {
        return ioExecutor.submit(
            Callable {
                orderService.orderProducts(OrderRequest(productIds))
                    .executeAsResult()
            },
        ).get()
    }
}
