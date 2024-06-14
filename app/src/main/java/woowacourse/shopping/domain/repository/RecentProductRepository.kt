package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct

interface RecentProductRepository {
    suspend fun save(product: Product)

    suspend fun loadLatest(): RecentProduct?

    suspend fun loadSecondLatest(): RecentProduct?

    suspend fun loadLatestList(): List<RecentProduct>
}
