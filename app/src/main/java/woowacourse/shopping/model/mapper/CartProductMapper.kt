package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.DomainCartProduct
import woowacourse.shopping.model.CartProductModel

fun CartProductModel.toDomain(): DomainCartProduct = DomainCartProduct(
    id = id,
    product = product.toDomain(),
    selectedCount = selectedCount.toDomain(),
    isChecked = isChecked,
)

fun DomainCartProduct.toUi(): CartProductModel = CartProductModel(
    id = id,
    product = product.toUi(),
    selectedCount = selectedCount.toUi(),
    isChecked = isChecked,
)

fun List<DomainCartProduct>.toUi(): List<CartProductModel> =
    map { cartProduct -> cartProduct.toUi() }

fun List<CartProductModel>.toDomain(): List<DomainCartProduct> =
    map { cartProduct -> cartProduct.toDomain() }
