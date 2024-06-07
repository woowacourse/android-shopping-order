package woowacourse.shopping.data.order

import woowacourse.shopping.data.common.ResponseResult

interface OrderDataSource {
    suspend fun orderCartItems(cartItemIds: List<Long>): ResponseResult<Unit>
}
