package woowacourse.shopping.domain.mapper

import woowacourse.shopping.data.model.RecentlyProductEntity
import woowacourse.shopping.domain.model.product.RecentlyProduct

object ProductEntityMapper {
    fun RecentlyProductEntity.toRecentlyProduct(): RecentlyProduct {
        return RecentlyProduct(
            id = id,
            productId = productId,
            imageUrl = imageUrl,
            name = name,
            category = category,
        )
    }

    fun RecentlyProduct.toRecentlyProductEntity(): RecentlyProductEntity {
        return RecentlyProductEntity(
            productId = productId,
            name = name,
            imageUrl = imageUrl,
            category = category,
        )
    }
}
