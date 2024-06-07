package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.OrderDataSourceImpl
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.OrderRepository

class RemoteOrderRepositoryImpl(
    private val orderDataSource: OrderDataSource = OrderDataSourceImpl(),
) : OrderRepository {
    override suspend fun orderShoppingCart(ids: List<Int>): Result<Unit> {
        return try {
            val response = orderDataSource.orderItems(ids = ids)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                throw RuntimeException(response.code().toString())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCoupons(): Result<List<Coupon>> {
        return try {
            val response = orderDataSource.getCoupons()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(RuntimeException(response.code().toString()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
