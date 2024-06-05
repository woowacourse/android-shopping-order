package woowacourse.shopping.data.order

import woowacourse.shopping.data.ResponseResult

interface OrderDataSource {

    fun order(cartItemIds: List<Long>): ResponseResult<Unit>
}