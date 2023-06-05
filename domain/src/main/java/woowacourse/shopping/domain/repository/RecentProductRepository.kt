package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.RecentProducts

interface RecentProductRepository {
    fun getRecentProducts(size: Int): RecentProducts
    fun saveRecentProduct(recentProduct: RecentProduct)
}
