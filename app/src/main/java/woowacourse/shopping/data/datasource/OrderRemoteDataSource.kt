package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.request.RequestOrdersPostDto

interface OrderRemoteDataSource {
    fun order(request: RequestOrdersPostDto): Result<Unit>
}
