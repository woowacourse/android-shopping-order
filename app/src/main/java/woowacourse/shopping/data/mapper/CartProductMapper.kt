package woowacourse.shopping.data.mapper

import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.data.cart.CartRemoteDataModel

fun CartRemoteDataModel.toDomain(): CartProductInfo {
    return CartProductInfo(
        product = product.toDomain(),
        count = count
    )
}
