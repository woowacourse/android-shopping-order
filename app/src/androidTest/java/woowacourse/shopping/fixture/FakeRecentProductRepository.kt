package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class FakeRecentProductRepository(
    initialRecentProductIds: List<Long> = emptyList(),
    private val recentProductSavedLimit: Int = 10,
) : RecentProductRepository {
    private val recentProductIds = initialRecentProductIds.toMutableList()

    override fun getRecentProducts(
        limit: Int,
        onResult: (Result<List<Product>>) -> Unit,
    ) {
        val products =
            recentProductIds
                .take(limit)
                .mapNotNull { id -> productsFixture.find { it.id == id }?.toDomain() }
        onResult(Result.success(products))
    }

    override fun insertAndTrimToLimit(
        productId: Long,
        category: String,
        onResult: (Result<Unit>) -> Unit,
    ) {
        recentProductIds.remove(productId)
        recentProductIds.add(0, productId)

        if (recentProductIds.size > recentProductSavedLimit) {
            recentProductIds.subList(recentProductSavedLimit, recentProductIds.size).clear()
        }

        onResult(Result.success(Unit))
    }
}
