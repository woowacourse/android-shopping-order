package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.OrderCartModel

fun CartProductModel.toOrderCartInfo(): OrderCartModel {
    return OrderCartModel(
        id = id,
        productModel = productModel,
        count = count

    )
}
