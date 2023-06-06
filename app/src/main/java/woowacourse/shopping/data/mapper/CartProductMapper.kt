package woowacourse.shopping.data.mapper

import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.data.remote.cart.CartDataModel

fun CartDataModel.toDomain(): CartProductInfo {
    return CartProductInfo(
        id = id,
        product = product.toDomain(),
        count = count,
    )
}

fun CartProductInfo.toDataModel(): CartDataModel {
    return CartDataModel(
        id = id,
        product = product.toDataModel(),
        count = count,
    )
}
