package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.request.RequestOrderPostDto
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

interface OrderDataSource {
    suspend fun postOrder(request: RequestOrderPostDto): Result<Unit, DataError>
}
