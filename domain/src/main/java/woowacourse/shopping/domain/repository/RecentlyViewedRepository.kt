package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyViewedProduct

interface RecentlyViewedRepository {
    fun getRecentlyViewedProducts(unit: Int): List<RecentlyViewedProduct>
    fun getLastViewedProduct(): Result<RecentlyViewedProduct>
    fun addRecentlyViewedProduct(product: Product): Long
}
