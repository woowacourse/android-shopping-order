package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

interface CartDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<ResponseCartItemsGetDto, DataError>

    suspend fun postCartItems(request: RequestCartItemPostDto): Result<Unit, DataError>

    suspend fun deleteCartItems(id: Long): Result<Unit, DataError>

    suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ): Result<Unit, DataError>

    suspend fun getCartItemCounts(): Result<ResponseCartItemCountsGetDto, DataError>
}
