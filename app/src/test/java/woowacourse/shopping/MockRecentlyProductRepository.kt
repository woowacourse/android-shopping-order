package woowacourse.shopping

import woowacourse.shopping.domain.model.product.RecentlyProduct
import woowacourse.shopping.domain.repository.RecentlyProductRepository

class MockRecentlyProductRepository : RecentlyProductRepository {
    override suspend fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getMostRecentlyProduct(): Result<RecentlyProduct> {
        return Result.success(RecentlyProduct.defaultRecentlyProduct)
    }

    override suspend fun getRecentlyProductList(): Result<List<RecentlyProduct>> {
        return Result.success(listOf(RecentlyProduct.defaultRecentlyProduct))
    }

    override suspend fun deleteRecentlyProduct(id: Long) = Result.success(Unit)
}
