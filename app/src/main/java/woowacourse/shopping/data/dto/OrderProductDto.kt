package woowacourse.shopping.data.dto

import woowacourse.shopping.domain.model.OrderProduct

fun OrderProduct.toOrderPostInfo(): OrderPostInfo = OrderPostInfo(
    cartItemId = cartProductId,
)

