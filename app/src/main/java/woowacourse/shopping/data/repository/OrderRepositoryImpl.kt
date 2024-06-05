package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dto.OrderRequest
import woowacourse.shopping.data.remote.datasource.OrderDataSourceImpl
import woowacourse.shopping.domain.repository.OrderRepository
import kotlin.concurrent.thread

class OrderRepositoryImpl(
    private val orderDataSourceImpl: OrderDataSourceImpl,
) : OrderRepository {
    override fun postOrder(cartItemIds: List<Int>): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            orderDataSourceImpl.postOrder(OrderRequest(cartItemIds))
                .onSuccess { result = Result.success(Unit) }
                .onFailure { result = Result.failure(IllegalArgumentException()) }
        }.join()

        return result ?: throw Exception()
    }
}
