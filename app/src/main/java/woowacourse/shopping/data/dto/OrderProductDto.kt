package woowacourse.shopping.data.dto

import woowacourse.shopping.domain.model.OrderProduct

fun OrderProduct.toOrderProductRequest(): OrderInfo = OrderInfo(
    productId = productId,
    quantity = quantity.value,
)

