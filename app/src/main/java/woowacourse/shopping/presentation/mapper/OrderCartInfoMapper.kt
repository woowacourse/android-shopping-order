package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.OrderCartInfo
import woowacourse.shopping.presentation.model.OrderCartModel

fun OrderCartModel.toDomain(): OrderCartInfo {
    return OrderCartInfo(
        cartId = id,
        product = productModel.toDomain(),
        count = count
    )
}
