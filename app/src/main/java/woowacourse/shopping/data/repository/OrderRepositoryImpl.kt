package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ApiHandleOrderDataSource
import woowacourse.shopping.data.datasource.impl.ApiHandleOrderDataSourceImpl
import woowacourse.shopping.data.remote.dto.request.RequestOrderPostDto
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.response.Response
import woowacourse.shopping.domain.response.handleApiResult

class OrderRepositoryImpl(private val dataSource: ApiHandleOrderDataSource = ApiHandleOrderDataSourceImpl()) :
    OrderRepository {
    override suspend fun order(cartIds: List<Long>): Response<Unit> = handleApiResult(
        dataSource.postOrder(RequestOrderPostDto(cartIds))
    )
}
