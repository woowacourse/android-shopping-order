package woowacourse.shopping.data.cart.order

import woowacourse.shopping.data.util.executeAsResult
import woowacourse.shopping.remote.dto.request.OrderRequest
import woowacourse.shopping.remote.service.OrderService
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService

interface OrderDataSource {
    fun orderProducts(productIds: List<Long>): Result<Unit>
}


