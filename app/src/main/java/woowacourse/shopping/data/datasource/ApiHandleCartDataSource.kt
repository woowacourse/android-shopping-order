package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.api.ApiResponse
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto

interface ApiHandleCartDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): ApiResponse<ResponseCartItemsGetDto>

    suspend fun postCartItems(request: RequestCartItemPostDto): ApiResponse<Unit>

    suspend fun deleteCartItems(id: Long): ApiResponse<Unit>

    suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ): ApiResponse<Unit>

    suspend fun getCartItemCounts(): ApiResponse<ResponseCartItemCountsGetDto>
}
