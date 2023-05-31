package woowacourse.shopping.data.dto

import woowacourse.shopping.domain.model.OrderProduct

fun OrderProduct.toOrderProductRequest(): OrderPostInfo = OrderPostInfo(
    productId = productId,
    quantity = quantity.value,
)

