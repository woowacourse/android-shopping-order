package woowacourse.shopping.data.entity.mapper

import woowacourse.shopping.data.entity.RecentProductEntity
import woowacourse.shopping.domain.model.RecentProduct

fun RecentProductEntity.toDomain(): RecentProduct =
    RecentProduct(id = id, product = product.toDomain())

fun RecentProduct.toEntity(): RecentProductEntity =
    RecentProductEntity(id = id, product = product.toEntity())

fun List<RecentProductEntity>.toDomain(): List<RecentProduct> = map { it.toDomain() }
