package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.local.recentlyViewed.RecentlyViewedEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyViewedProduct

object RecentlyViewedMapper {
    fun RecentlyViewedEntity.toDomainModel() = RecentlyViewedProduct(
        viewedDateTime = viewedDateTime,
        product = Product(
            id = productId,
            name = name,
            imageUrl = imageUrl,
            price = price,
        ),
    )
}
