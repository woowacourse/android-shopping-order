package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.dto.CartGetResponse
import woowacourse.shopping.domain.model.DomainCartProduct
import woowacourse.shopping.domain.model.ProductCount

fun CartGetResponse.toDomain(): DomainCartProduct = DomainCartProduct(
    id = id,
    product = product.toDomain(),
    selectedCount = ProductCount(quantity),
    isChecked = true,
)

fun List<CartGetResponse>.toDomain(): List<DomainCartProduct> = map { it.toDomain() }
