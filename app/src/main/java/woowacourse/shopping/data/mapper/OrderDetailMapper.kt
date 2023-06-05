package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.order.response.OrderDetailDataModel
import woowacourse.shopping.presentation.model.OrderDetailModel

fun OrderDetailDataModel.toPresentation(): OrderDetailModel {
    return OrderDetailModel(
        orderId = orderId,
        totalPrice = totalPrice,
        spendPoint = spendPoint,
        spendPrice = spendPrice,
        orderDate = createdAt,
        orderItems = orderItemResponses.map { it.toPresentation() }
    )
}
