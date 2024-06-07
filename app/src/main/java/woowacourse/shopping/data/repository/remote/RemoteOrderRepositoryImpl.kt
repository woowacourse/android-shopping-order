package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.OrderDataSourceImpl
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.exception.NoSuchDataException

class RemoteOrderRepositoryImpl(
    private val orderDataSource: OrderDataSource = OrderDataSourceImpl(),
) : OrderRepository {
    override suspend fun orderShoppingCart(ids: List<Int>): Result<Unit> {
        return try {
            val response = orderDataSource.orderItems(ids = ids)
            if (!response.isSuccessful && response.body() != null) {
                throw NoSuchDataException()
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
