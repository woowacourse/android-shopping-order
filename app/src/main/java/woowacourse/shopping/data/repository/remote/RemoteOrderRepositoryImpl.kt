package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.OrderDataSourceImpl
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.exception.OrderItemsException

class RemoteOrderRepositoryImpl(
    private val orderDataSource: OrderDataSource = OrderDataSourceImpl(),
) : OrderRepository {
    override suspend fun orderShoppingCart(ids: List<Long>): Result<Unit> =
        runCatching {
            val response = orderDataSource.orderItems(ids = ids)
            if (response.isSuccessful) {
                Unit
            } else {
                throw OrderItemsException()
            }
        }

    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            val response = orderDataSource.getCoupons()
            if (response.isSuccessful && response.body() != null) {
                response.body() ?: emptyList()
            } else {
                throw RuntimeException(response.code().toString())
            }
        }
}
