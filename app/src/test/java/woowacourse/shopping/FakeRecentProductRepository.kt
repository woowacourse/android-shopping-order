package woowacourse.shopping

import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class FakeRecentProductRepository(savedRecentProductEntities: List<RecentProductEntity> = emptyList()) :
    RecentProductRepository {
    private val recentProductsEntity: MutableMap<Int, RecentProductEntity> =
        savedRecentProductEntities
            .associateBy { it.productId }
            .toMutableMap()
    private var id: Int = 0

    override fun findLastOrNull(): RecentProductEntity? {
        if (recentProductsEntity.isEmpty()) return null
        return recentProductsEntity.entries.last().value
    }

    override fun findRecentProducts(): List<RecentProductEntity> {
        return recentProductsEntity.asSequence()
            .map { it.value }
            .take(FIND_RECENT_PRODUCTS_COUNT)
            .toList()
            .reversed()
    }

    override fun save(productId: Int) {
        if (recentProductsEntity.contains(productId)) {
            recentProductsEntity.remove(productId)
        }
        recentProductsEntity[productId] = RecentProductEntity(id++, productId, LocalDateTime.now())
    }

    companion object {
        private const val FIND_RECENT_PRODUCTS_COUNT = 10
    }
}
