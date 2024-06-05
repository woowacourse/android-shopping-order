package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto

interface CartDataSource {
    fun getCartItems(
        page: Int,
        size: Int,
    ): ResponseCartItemsGetDto?

    fun postCartItems(request: RequestCartItemPostDto)

    fun deleteCartItems(id: Long)

    fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    )

    fun getCartItemCounts(): ResponseCartItemCountsGetDto?
}
