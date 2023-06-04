package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.dto.CartGetResponse
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount

fun CartGetResponse.toDomain(): CartProduct = CartProduct(
    id = id,
    product = product.toDomain(),
    selectedCount = ProductCount(quantity),
    isChecked = true,
)

fun List<CartGetResponse>.toDomain(): List<CartProduct> = map { it.toDomain() }
