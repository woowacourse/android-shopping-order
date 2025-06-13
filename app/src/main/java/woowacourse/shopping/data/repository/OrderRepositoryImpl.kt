package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.data.di.ApiResult
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val dataSource: OrderRemoteDataSource,
) : OrderRepository {
    override suspend fun postOrderProducts(cartIds: List<Long>): Result<Unit> =
        when (val result = dataSource.postOrderProducts(cartIds)) {
            is ApiResult.Success -> Result.success(Unit)
            is ApiResult.ClientError -> Result.failure(Exception("Client error: ${result.code} ${result.message}"))
            is ApiResult.ServerError -> Result.failure(Exception("Server error: ${result.code} ${result.message}"))
            is ApiResult.NetworkError -> Result.failure(result.throwable)
            ApiResult.UnknownError -> Result.failure(Exception("Unknown error"))
        }
}
