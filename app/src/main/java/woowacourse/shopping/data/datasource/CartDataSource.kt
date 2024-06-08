package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto

interface CartDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): ResponseCartItemsGetDto

    suspend fun postCartItems(request: RequestCartItemPostDto)

    suspend fun deleteCartItems(id: Long)

    suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    )

    suspend fun getCartItemCounts(): ResponseCartItemCountsGetDto
}
