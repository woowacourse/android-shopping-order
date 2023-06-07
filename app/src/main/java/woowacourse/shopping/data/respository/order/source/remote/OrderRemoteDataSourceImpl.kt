package woowacourse.shopping.data.respository.order.source.remote

import woowacourse.shopping.data.model.dto.request.OrderRequest
import woowacourse.shopping.data.model.dto.response.OrderDetailResponse
import woowacourse.shopping.data.model.dto.response.PointResponse
import woowacourse.shopping.data.model.dto.response.SavingPointResponse
import woowacourse.shopping.data.respository.order.service.OrderService
import woowacourse.shopping.data.util.responseBodyCallback
import woowacourse.shopping.data.util.responseHeaderLocationCallback
import woowacouse.shopping.model.order.Order

class OrderRemoteDataSourceImpl(
    private val orderService: OrderService,
) : OrderRemoteDataSource {
    override fun requestPostData(
        order: Order,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Long) -> Unit
    ) {
        val orderRequest = OrderRequest(
            order.cartIds,
            order.getCardNumber(),
            order.card.cvc,
            order.usePoint.getPoint()
        )
        orderService.requestPostData(orderRequest).enqueue(
            responseHeaderLocationCallback(
                onFailure = onFailure,
                onSuccess = onSuccess
            )
        )
    }

    override fun requestOrderItem(
        orderId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (OrderDetailResponse) -> Unit
    ) {
        orderService.requestOrderItem(orderId).enqueue(
            responseBodyCallback(
                onFailure = onFailure,
                onSuccess = onSuccess
            )
        )
    }

    override fun requestOrderList(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (List<OrderDetailResponse>) -> Unit
    ) {
        orderService.requestOrderList().enqueue(
            responseBodyCallback(
                onFailure = onFailure,
                onSuccess = onSuccess
            )
        )
    }

    override fun requestPoint(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (PointResponse) -> Unit
    ) {
        orderService.requestPoint().enqueue(
            responseBodyCallback(
                onFailure = onFailure,
                onSuccess = onSuccess
            )
        )
    }

    override fun requestPredictionSavePoint(
        orderPrice: Int,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (SavingPointResponse) -> Unit
    ) {
        orderService.requestPredictionSavePoint(orderPrice).enqueue(
            responseBodyCallback(
                onFailure = onFailure,
                onSuccess = onSuccess
            )
        )
    }
}
