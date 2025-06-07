package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class FakeRecentProductRepository(
    initialRecentProductIds: List<Long> = emptyList(),
    private val recentProductSavedLimit: Int = 10,
) : RecentProductRepository {
    private val recentProductIds = initialRecentProductIds.toMutableList()

    override suspend fun getRecentProducts(limit: Int): Result<List<Product>> {
        val products =
            recentProductIds
                .take(limit)
                .mapNotNull { id -> productsFixture.find { it.id == id }?.toDomain() }
        return Result.success(products)
    }

    override suspend fun insertAndTrimToLimit(
        productId: Long,
        category: String,
        recentProductLimit: Int,
    ): Result<Unit> {
        recentProductIds.remove(productId)
        recentProductIds.add(0, productId)

        if (recentProductIds.size > recentProductSavedLimit) {
            recentProductIds.subList(recentProductSavedLimit, recentProductIds.size).clear()
        }

        return Result.success(Unit)
    }
}
