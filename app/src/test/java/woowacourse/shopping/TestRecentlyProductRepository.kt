package woowacourse.shopping

import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.repository.RecentlyProductRepository

class TestRecentlyProductRepository : RecentlyProductRepository {
    override suspend fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Long> = Result.success(0L)

    override suspend fun getMostRecentlyProduct(): Result<RecentlyProduct> {
        return Result.success(RecentlyProduct.defaultRecentlyProduct)
    }

    override suspend fun getRecentlyProductList(): Result<List<RecentlyProduct>> {
        return Result.success(listOf(RecentlyProduct.defaultRecentlyProduct))
    }

    override suspend fun deleteRecentlyProduct(id: Long): Result<Unit> {
        return Result.success(Unit)
    }
}
