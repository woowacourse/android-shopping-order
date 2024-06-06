package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.api.ApiResult
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto

interface ApiHandleCartDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): ApiResult<ResponseCartItemsGetDto>

    suspend fun postCartItems(request: RequestCartItemPostDto): ApiResult<Unit>

    suspend fun deleteCartItems(id: Long): ApiResult<Unit>

    suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ): ApiResult<Unit>

    suspend fun getCartItemCounts(): ApiResult<ResponseCartItemCountsGetDto>
}
