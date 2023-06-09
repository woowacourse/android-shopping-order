package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.local.recentlyViewed.RecentlyViewedEntity
import woowacourse.shopping.domain.model.Product

interface RecentlyViewedDataSource {
    fun getRecentlyViewedProducts(unit: Int): List<RecentlyViewedEntity>
    fun getLastViewedProduct(): RecentlyViewedEntity?
    fun addRecentlyViewedProduct(product: Product): Long
}
