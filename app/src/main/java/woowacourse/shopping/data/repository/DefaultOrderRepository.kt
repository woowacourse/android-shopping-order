package woowacourse.shopping.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.network.request.OrderRequest
import woowacourse.shopping.domain.repository.OrderRepository

class DefaultOrderRepository(
    private val dataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun createOrder(ids: List<Long>) = withContext(Dispatchers.IO) {
        dataSource.createOrder(OrderRequest(ids))
    }
}
