package woowacourse.shopping.data.respository.order.source.remote

import woowacourse.shopping.data.model.dto.response.OrderDetailResponse
import woowacourse.shopping.data.model.dto.response.PointResponse
import woowacourse.shopping.data.model.dto.response.SavingPointResponse
import woowacouse.shopping.model.order.Order

interface OrderRemoteDataSource {
    fun requestPostData(
        order: Order,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Long) -> Unit,
    )

    fun requestOrderItem(
        orderId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (OrderDetailResponse) -> Unit,
    )

    fun requestOrderList(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (List<OrderDetailResponse>) -> Unit
    )

    fun requestPoint(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (PointResponse) -> Unit,
    )

    fun requestPredictionSavePoint(
        orderPrice: Int,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (SavingPointResponse) -> Unit,
    )
}
