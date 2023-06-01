package woowacourse.shopping.data.dto

import woowacourse.shopping.domain.model.OrderProduct

fun OrderProduct.toOrderPostInfo(): OrderItem = OrderItem(
    cartItemId = cartProductId,
)

