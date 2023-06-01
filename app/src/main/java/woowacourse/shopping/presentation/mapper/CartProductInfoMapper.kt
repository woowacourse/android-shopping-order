package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.presentation.model.OrderCartInfoModel

fun CartProductInfoModel.toOrderCartInfo(): OrderCartInfoModel {
    return OrderCartInfoModel(
        id = id,
        productModel = productModel,
        count = count

    )
}
