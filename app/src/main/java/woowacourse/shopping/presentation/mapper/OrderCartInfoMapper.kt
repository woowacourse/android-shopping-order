package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.OrderCartInfo
import woowacourse.shopping.presentation.model.OrderCartInfoModel

fun OrderCartInfoModel.toDomain(): OrderCartInfo {
    return OrderCartInfo(
        cartId = id,
        product = productModel.toDomain(),
        count = count
    )
}
