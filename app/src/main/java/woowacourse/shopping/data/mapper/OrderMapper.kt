package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.OrderRequest
import woowacourse.shopping.data.dto.toOrderProductRequest
import woowacourse.shopping.domain.model.Order

fun Order.toOrderRequest(): OrderRequest = OrderRequest(
    orderInfos = orderProducts.map { it.toOrderProductRequest() },
    payment = totalPayment.value,
    point = usePoint.value,
)
