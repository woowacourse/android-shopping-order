package woowacourse.shopping

import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
import woowacourse.shopping.data.recent.local.toProductEntity
import woowacourse.shopping.data.recent.local.toRecentProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class FakeRecentProductRepository : RecentProductRepository {
    private val recentProductsEntity: MutableMap<Int, RecentProductEntity> = mutableMapOf()
    private var id: Int = 0

    override fun findLastOrNull(): RecentProduct? {
        if (recentProductsEntity.isEmpty()) return null
        return recentProductsEntity.entries.last().value.toRecentProduct()
    }

    override fun findRecentProducts(): List<RecentProduct> {
        return recentProductsEntity.asSequence()
            .map { it.value.toRecentProduct() }
            .take(FIND_RECENT_PRODUCTS_COUNT)
            .toList()
            .reversed()
    }

    override fun save(product: Product) {
        if (recentProductsEntity.contains(product.id)) {
            recentProductsEntity.remove(product.id)
        }
        recentProductsEntity[product.id] =
            RecentProductEntity(id++, product.toProductEntity(), LocalDateTime.now())
    }

    companion object {
        private const val FIND_RECENT_PRODUCTS_COUNT = 10
    }
}
