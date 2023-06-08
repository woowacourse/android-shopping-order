package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.dto.OrderHistoryDto
import woowacourse.shopping.data.dto.OrderInfoDto
import woowacourse.shopping.data.dto.OrderListResponse
import woowacourse.shopping.data.dto.OrderRequest

interface OrderDataSource {
    fun getOrderItemsInfo(
        ids: List<Int>,
        onSuccess: (OrderInfoDto) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun postOrderItem(
        orderRequest: OrderRequest,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun getOrderHistories(
        onSuccess: (OrderListResponse) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun getOrderHistory(
        id: Int,
        onSuccess: (OrderHistoryDto) -> Unit,
        onFailure: (Exception) -> Unit,
    )
}
