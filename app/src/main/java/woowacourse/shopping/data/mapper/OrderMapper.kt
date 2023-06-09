package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.dto.response.OrderDetailResponse
import woowacouse.shopping.model.order.OrderDetail

fun OrderDetailResponse.toModel(): OrderDetail = OrderDetail(
    id,
    usedPoint,
    savedPoint,
    orderedAt,
    products.map { it.toModel() }
)
