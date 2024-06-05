package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto

interface CartRemoteDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<ResponseCartItemsGetDto>

    suspend fun postCartItems(request: RequestCartItemPostDto): Result<Unit>

    fun deleteCartItems(id: Long): Result<Unit>

    suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ): Result<Unit>

    suspend fun getCartItemCounts(): Result<ResponseCartItemCountsGetDto>
}
