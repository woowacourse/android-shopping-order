package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct

interface RecentProductRepository {
    fun findLastOrNull(): RecentProduct?

    fun findRecentProducts(): List<RecentProduct>

    fun save(product: Product)
}
