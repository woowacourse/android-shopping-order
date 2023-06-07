package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.response.BasketProductEntity
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count

fun BasketProductEntity.toDomainModel() = BasketProduct(
    id = id,
    count = Count(count),
    product = product.toDomainModel()
)

fun BasketProduct.toEntity() = BasketProductEntity(
    id = id,
    count = count.value,
    product = product.toEntity()
)
