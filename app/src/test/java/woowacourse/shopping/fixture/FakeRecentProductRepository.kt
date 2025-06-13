package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class FakeRecentProductRepository : RecentProductRepository {
    private val recentProducts = mutableListOf<RecentProduct>()

    override suspend fun getPagedProducts(
        limit: Int,
        offset: Int,
    ): Result<List<RecentProduct>> =
        Result.success(
            recentProducts
                .sortedByDescending { it.viewedAt }
                .drop(offset)
                .take(limit),
        )

    override suspend fun saveRecentlyViewedProduct(recentProduct: RecentProduct): Result<Unit> {
        recentProducts.removeIf { it.product.id == recentProduct.product.id }
        recentProducts.add(recentProduct)
        return Result.success(Unit)
    }
}
