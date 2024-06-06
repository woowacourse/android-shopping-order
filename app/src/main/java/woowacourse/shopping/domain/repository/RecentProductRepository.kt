package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.Product
import woowacourse.shopping.domain.model.RecentProduct

interface RecentProductRepository {
    suspend fun save(product: Product)

    suspend fun update(productId: Int)

    suspend fun findOrNullByProductId(productId: Int): RecentProduct?

    suspend fun findMostRecentProduct(): RecentProduct?

    suspend fun findAll(limit: Int): List<RecentProduct>

    suspend fun deleteAll()
}
