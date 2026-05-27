package woowacourse.shopping.data.localdb.mapper

import woowacourse.shopping.data.localdb.entity.RecentItemEntity
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.RecentItem

fun RecentItemEntity.toDomain(): RecentItem =
    RecentItem(
        productId = productId,
        name = name,
        imageUrl = imageUrl,
    )

fun Product.toRecentItemEntity(timestamp: Long): RecentItemEntity =
    RecentItemEntity(
        productId = id,
        name = getName(),
        imageUrl = imageUrl,
        timestamp = timestamp,
    )
