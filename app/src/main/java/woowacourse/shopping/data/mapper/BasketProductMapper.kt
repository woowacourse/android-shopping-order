package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.BasketProductEntity
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count

fun BasketProductEntity.toBasketProductDomainModel() = BasketProduct(
    id = id,
    count = Count(count),
    product = product.toProductDomainModel()
)

fun BasketProduct.toBasketProductEntity() = BasketProductEntity(
    id = id,
    count = count.value,
    product = product.toProductEntity()
)
