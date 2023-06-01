package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.RecentProductEntity
import woowacourse.shopping.domain.RecentProduct

fun RecentProductEntity.toRecentProductDomainModel() = RecentProduct(
    id = id,
    product = product.toProductDomainModel()
)

fun RecentProduct.toRecentProductEntity() = RecentProductEntity(
    id = id,
    product = product.toProductEntity()
)
