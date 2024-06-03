package woowacourse.shopping

import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.repository.RecentlyProductRepository

class MockRecentlyProductRepository : RecentlyProductRepository {
    override fun addRecentlyProduct(recentlyProduct: RecentlyProduct): Result<Unit> {
        return Result.success(Unit)
    }

    override fun getMostRecentlyProduct(): Result<RecentlyProduct> {
        return Result.success(RecentlyProduct.defaultRecentlyProduct)
    }

    override fun getRecentlyProductList(): Result<List<RecentlyProduct>> {
        return Result.success(listOf(RecentlyProduct.defaultRecentlyProduct))
    }

    override fun deleteRecentlyProduct(id: Long) = Result.success(Unit)
}
