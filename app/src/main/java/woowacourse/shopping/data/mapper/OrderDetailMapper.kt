package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.OrderProductEntity
import woowacourse.shopping.ui.model.OrderProductUiModel

// todo data layer에서 왜 ui를?
// todo 지금은 분리의 의미가 없긴하다.
fun OrderProductEntity.toOrderProductUiModel() = OrderProductUiModel(
    productId = productId,
    productName = productName,
    quantity = quantity,
    price = price,
    imageUrl = imageUrl,
)
