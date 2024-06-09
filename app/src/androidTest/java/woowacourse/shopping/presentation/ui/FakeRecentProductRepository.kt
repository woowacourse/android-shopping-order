package woowacourse.shopping.presentation.ui

import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.RecentProductRepository

class FakeRecentProductRepository :
    RecentProductRepository {
    override suspend fun findAllByLimit(limit: Int): Result<List<RecentProduct>> {
        TODO("Not yet implemented")
    }

    override suspend fun findOrNull(): Result<RecentProduct?> {
        TODO("Not yet implemented")
    }

    override suspend fun save(recentProduct: RecentProduct): Result<Long> {
        TODO("Not yet implemented")
    }
}
