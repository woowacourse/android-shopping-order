package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.DataOrderProduct
import woowacourse.shopping.ui.model.OrderProduct

// todo 지금은 분리의 의미가 없긴하다.
fun DataOrderProduct.toOrderDetail() = OrderProduct(
    productId = productId,
    productName = productName,
    quantity = quantity,
    price = price,
    imageUrl = imageUrl,
)
