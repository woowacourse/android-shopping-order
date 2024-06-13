package woowacourse.shopping.fake

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class FakeRecentProductRepository(savedRecentProducts: List<RecentProduct> = emptyList()) : RecentProductRepository {
    private val recentProducts: MutableList<RecentProduct> = savedRecentProducts.toMutableList()
    private var id: Int = 0

    override suspend fun findLastOrNull(): Result<RecentProduct?> {
        if (recentProducts.isEmpty()) return Result.success(null)
        return Result.success(recentProducts.last())
    }

    override suspend fun findRecentProducts(): Result<List<RecentProduct>> {
        val recentProducts =
            recentProducts.asSequence()
                .take(FIND_RECENT_PRODUCTS_COUNT)
                .toList()
                .reversed()
        return Result.success(recentProducts)
    }

    override suspend fun save(product: Product): Result<Unit> {
        val recentProduct = recentProducts.find { it.product.id == product.id }
        if (recentProduct != null) {
            recentProducts.remove(recentProduct)
        }
        recentProducts.add(RecentProduct(id++, product))
        return Result.success(Unit)
    }

    companion object {
        private const val FIND_RECENT_PRODUCTS_COUNT = 10
    }
}
