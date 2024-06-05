package woowacourse.shopping.data.order

import woowacourse.shopping.data.NetworkResult

interface OrderDataSource {
    fun requestOrder(
        cartItemIds: List<Int>,
        callBack: (NetworkResult<Unit>) -> Unit,
    )
}
