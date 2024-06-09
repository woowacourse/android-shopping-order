package woowacourse.shopping.domain.repository.order

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.Product

interface OrderRepository {
    suspend fun orderCartItems(cartItemIds: List<Long>): ResponseResult<Unit>

    suspend fun loadRecommendedProducts(): ResponseResult<List<Product>>
}
