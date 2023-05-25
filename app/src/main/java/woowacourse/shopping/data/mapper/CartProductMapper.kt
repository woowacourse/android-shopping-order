package woowacourse.shopping.data.mapper

import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.data.cart.CartRemoteDataModel

fun CartRemoteDataModel.toDomain(): CartProductInfo {
    return CartProductInfo(
        id = id,
        product = product.toDomain(),
        count = count,
    )
}

fun CartProductInfo.toDataModel(): CartRemoteDataModel {
    return CartRemoteDataModel(
        id = id,
        product = product.toDataModel(),
        count = count,
    )
}
