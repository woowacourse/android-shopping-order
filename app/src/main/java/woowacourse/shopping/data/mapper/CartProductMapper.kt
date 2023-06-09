package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.CartProductDto
import woowacourse.shopping.data.model.DataCartProduct
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.DomainCartProduct
import woowacourse.shopping.domain.model.ProductCount

fun DataCartProduct.toDomain(): CartProduct = CartProduct(
    id = id,
    product = product.toDomain(),
    selectedCount = selectedCount.toDomain(),
    isChecked = isChecked == 1,
)

fun CartProduct.toData(): DataCartProduct = DataCartProduct(
    id = id,
    product = product.toData(),
    selectedCount = selectedCount.toData(),
    isChecked = if (isChecked) 1 else 0,
)

fun List<DomainCartProduct>.toData(): List<DataCartProduct> = map { it.toData() }

fun CartProductDto.toDomain(): CartProduct = CartProduct(
    id = id,
    product = product.toDomain(),
    selectedCount = ProductCount(quantity),
    isChecked = true,
)
