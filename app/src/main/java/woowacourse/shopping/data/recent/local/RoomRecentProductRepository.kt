package woowacourse.shopping.data.recent.local

import woowacourse.shopping.data.recent.local.dao.RecentProductDao
import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime
import kotlin.concurrent.thread

class RoomRecentProductRepository(private val recentProductDao: RecentProductDao) :
    RecentProductRepository {
    override fun findLastOrNull(): RecentProduct? {
        var lastRecentProductEntity: RecentProductEntity? = null
        thread {
            lastRecentProductEntity = recentProductDao.findRange(1).firstOrNull()
        }.join()
        return lastRecentProductEntity?.toRecentProduct()
    }

    override fun findRecentProducts(): List<RecentProduct> {
        var lastRecentProductEntity: List<RecentProductEntity> = emptyList()
        thread {
            lastRecentProductEntity = recentProductDao.findRange(FIND_RECENT_PRODUCTS_COUNT)
        }.join()
        return lastRecentProductEntity.toRecentProducts()
    }

    override fun save(product: Product) {
        thread {
            if (recentProductDao.findOrNull(product.id) == null) {
                recentProductDao.insert(
                    RecentProductEntity(
                        product = product.toProductEntity(),
                        seenDateTime = LocalDateTime.now(),
                    ),
                )
                return@thread
            }
            recentProductDao.update(product.id, LocalDateTime.now())
        }.join()
    }

    override fun getRecommendProducts(
        category: String,
        cartItems: List<CartItem>,
    ): List<Product> {
        var recommendProducts: List<Product> = emptyList()
        thread {
            val categoryProducts = recentProductDao.findCategory(category)
            val recommendCategoryProducts =
                categoryProducts.filter { recentProduct ->
                    cartItems.none { it.productId == recentProduct.product.productId }
                }
            recommendProducts =
                recommendCategoryProducts
                    .take(RECOMMEND_PRODUCTS_COUNT)
                    .map { it.toRecentProduct().product }
        }.join()
        return recommendProducts
    }

    companion object {
        private const val FIND_RECENT_PRODUCTS_COUNT = 10
        private const val RECOMMEND_PRODUCTS_COUNT = 10
    }
}
