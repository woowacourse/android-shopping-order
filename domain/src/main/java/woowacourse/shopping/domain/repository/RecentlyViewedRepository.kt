package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyViewedProduct
import woowacourse.shopping.domain.util.WoowaResult

interface RecentlyViewedRepository {
    fun getRecentlyViewedProducts(unit: Int): List<RecentlyViewedProduct>
    fun getLastViewedProduct(): WoowaResult<RecentlyViewedProduct>
    fun addRecentlyViewedProduct(product: Product): Long
}
