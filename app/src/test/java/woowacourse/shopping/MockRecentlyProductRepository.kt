package woowacourse.shopping

import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.repository.RecentlyProductRepository

class MockRecentlyProductRepository : RecentlyProductRepository {
    override suspend fun addRecentlyProduct(recentlyProduct: RecentlyProduct) {}

    override suspend fun getMostRecentlyProduct(): Result<RecentlyProduct> {
        return Result.runCatching { RecentlyProduct.defaultRecentlyProduct }
    }

    override suspend fun getRecentlyProductList(): Result<List<RecentlyProduct>> {
        return Result.runCatching { listOf(RecentlyProduct.defaultRecentlyProduct) }
    }

    override suspend fun deleteRecentlyProduct(id: Long) {}
}
