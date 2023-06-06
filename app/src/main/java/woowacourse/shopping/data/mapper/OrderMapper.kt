package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.order.response.OrderDataModel
import woowacourse.shopping.presentation.model.OrderModel

fun OrderDataModel.toPresentation(): OrderModel {
    return OrderModel(
        id = orderId,
        firstProductName = firstProductName,
        totalCount = totalCount,
        imageUrl = imageUrl,
        spendPrice = spendPrice,
        createAt = createdAt
    )
}
