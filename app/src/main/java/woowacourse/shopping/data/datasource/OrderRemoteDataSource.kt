package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.request.RequestOrdersPostDto

interface OrderRemoteDataSource {
    suspend fun order(request: RequestOrdersPostDto): Result<Unit>
}
