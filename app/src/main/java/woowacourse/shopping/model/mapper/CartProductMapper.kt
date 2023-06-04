package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.model.CartProductModel

fun CartProductModel.toDomain(): CartProduct = CartProduct(
    id = id,
    product = product.toDomain(),
    selectedCount = selectedCount.toDomain(),
    isChecked = isChecked,
)

fun CartProduct.toUi(): CartProductModel = CartProductModel(
    id = id,
    product = product.toUi(),
    selectedCount = selectedCount.toUi(),
    isChecked = isChecked,
)

fun List<CartProduct>.toUi(): List<CartProductModel> =
    map { cartProduct -> cartProduct.toUi() }

fun List<CartProductModel>.toDomain(): List<CartProduct> =
    map { cartProduct -> cartProduct.toDomain() }
