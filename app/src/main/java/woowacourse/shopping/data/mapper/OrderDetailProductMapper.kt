package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.order.response.OrderDetailProductDataModel
import woowacourse.shopping.presentation.model.OrderDetailProductModel

fun OrderDetailProductDataModel.toPresentation(): OrderDetailProductModel {
    return OrderDetailProductModel(
        productId = productId,
        name = name,
        imageUrl = imageUrl,
        price = price,
        quantity = quantity
    )
}
