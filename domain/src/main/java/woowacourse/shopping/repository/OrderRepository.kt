package woowacourse.shopping.repository

import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.OrderInfo

interface OrderRepository {
    fun getOrderInfo(
        ids: List<Int>,
        onSuccess: (OrderInfo) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun postOrder(
        ids: List<Int>,
        usedPoints: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun getOrderHistoryList(
        onSuccess: (List<OrderHistory>) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun getOrderHistory(
        id: Int,
        onSuccess: (OrderHistory) -> Unit,
        onFailure: (Exception) -> Unit,
    )
}
