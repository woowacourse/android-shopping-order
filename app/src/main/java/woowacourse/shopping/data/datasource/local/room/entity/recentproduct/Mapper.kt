package woowacourse.shopping.data.datasource.local.room.entity.recentproduct

import woowacourse.shopping.data.datasource.local.room.entity.product.toProduct
import woowacourse.shopping.data.datasource.local.room.entity.product.toProductEntity
import woowacourse.shopping.domain.model.RecentProduct

fun RecentProductEntity.toRecentProduct(): RecentProduct =
    RecentProduct(
        id = id,
        product = product.toProduct(),
        seenDateTime = seenDateTime,
    )

fun RecentProduct.toRecentProductEntity(): RecentProductEntity =
    RecentProductEntity(
        product = product.toProductEntity(),
        seenDateTime = seenDateTime,
    )

fun List<RecentProductEntity>.toRecentProducts(): List<RecentProduct> = map { it.toRecentProduct() }

fun List<RecentProduct>.toRecentProductsEntity(): List<RecentProductEntity> = map { it.toRecentProductEntity() }
