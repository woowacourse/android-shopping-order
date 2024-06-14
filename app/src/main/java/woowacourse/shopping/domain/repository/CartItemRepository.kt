package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct

interface CartItemRepository {
    suspend fun getAllByPaging(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>>

    suspend fun post(cartItemRequest: CartItemRequest): Result<Int>

    suspend fun patch(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Result<Unit>

    suspend fun delete(id: Int): Result<Unit>

    suspend fun getCount(): Result<Int>
}
