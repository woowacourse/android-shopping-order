package woowacourse.shopping.data.recent.local

import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
import woowacourse.shopping.domain.model.RecentProduct

fun RecentProductEntity.toRecentProduct() = RecentProduct(id, productId, seenDateTime)

fun List<RecentProductEntity>.toRecentProducts() = map { it.toRecentProduct() }

fun RecentProduct.toRecentProductEntity() = RecentProductEntity(id, productId, seenDateTime)

fun List<RecentProduct>.toRecentProductEntities() = map { it.toRecentProductEntity() }
