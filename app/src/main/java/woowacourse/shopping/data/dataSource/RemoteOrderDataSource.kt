package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.dto.OrderHistoryDto
import woowacourse.shopping.data.dto.OrderInfoDto
import woowacourse.shopping.data.dto.OrderListResponse
import woowacourse.shopping.data.dto.OrderRequest
import woowacourse.shopping.data.service.RetrofitClient
import woowacourse.shopping.data.service.RetrofitOrderService
import woowacourse.shopping.data.utils.createResponseCallback

class RemoteOrderDataSource(
    private val service: RetrofitOrderService = RetrofitClient.getInstance().retrofitOrderService,
) : OrderDataSource {
    override fun getOrderItemsInfo(
        ids: List<Int>,
        onSuccess: (OrderInfoDto) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        service.orderCart(ids).enqueue(
            createResponseCallback(
                onSuccess = onSuccess,
                onFailure = onFailure,
            ),
        )
    }

    override fun postOrderItem(
        orderRequest: OrderRequest,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        service.postOrderItem(orderRequest).enqueue(
            createResponseCallback(
                onSuccess = { onSuccess() },
                onFailure = onFailure,
            ),
        )
    }

    override fun getOrderHistories(
        onSuccess: (OrderListResponse) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        service.getOrders().enqueue(
            createResponseCallback(
                onSuccess = onSuccess,
                onFailure = onFailure,
            ),
        )
    }

    override fun getOrderHistory(
        id: Int,
        onSuccess: (OrderHistoryDto) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        service.getOrder(id).enqueue(
            createResponseCallback(
                onSuccess = onSuccess,
                onFailure = onFailure,
            ),
        )
    }
}
