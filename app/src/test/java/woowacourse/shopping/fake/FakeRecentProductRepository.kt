package woowacourse.shopping.fake

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class FakeRecentProductRepository(savedRecentProducts: List<RecentProduct> = emptyList()) : RecentProductRepository {
    private val recentProducts: MutableList<RecentProduct> = savedRecentProducts.toMutableList()
    private var id: Int = 0

    override fun findLastOrNull(): RecentProduct? {
        if (recentProducts.isEmpty()) return null
        return recentProducts.last()
    }

    override fun findRecentProducts(): List<RecentProduct> {
        return recentProducts.asSequence()
            .take(FIND_RECENT_PRODUCTS_COUNT)
            .toList()
            .reversed()
    }

    override fun save(product: Product) {
        val recentProduct = recentProducts.find { it.product.id == product.id }
        if (recentProduct != null) {
            recentProducts.remove(recentProduct)
        }
        recentProducts.add(RecentProduct(id++, product, LocalDateTime.now()))
    }

    companion object {
        private const val FIND_RECENT_PRODUCTS_COUNT = 10
    }
}
