package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.response.RecentProductEntity
import woowacourse.shopping.domain.RecentProduct

fun RecentProductEntity.toDomainModel() = RecentProduct(
    id = id,
    product = product.toDomainModel()
)

fun RecentProduct.toEntity() = RecentProductEntity(
    id = id,
    product = product.toEntity()
)
