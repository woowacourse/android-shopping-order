package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.api.ApiResponse
import woowacourse.shopping.data.remote.dto.request.RequestOrderPostDto

interface ApiHandleOrderDataSource {
    suspend fun postOrder(request: RequestOrderPostDto): ApiResponse<Unit>

}
