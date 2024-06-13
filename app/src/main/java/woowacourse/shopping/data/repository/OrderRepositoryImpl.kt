package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.datasource.impl.RemoteOrderDataSource
import woowacourse.shopping.data.remote.dto.request.RequestOrderPostDto
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

class OrderRepositoryImpl(private val dataSource: OrderDataSource = RemoteOrderDataSource()) :
    OrderRepository {
    override suspend fun order(cartIds: List<Long>): Result<Unit, DataError> =
        dataSource.postOrder(RequestOrderPostDto(cartIds))
}
