package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.database.RecentProductDatabase
import woowacourse.shopping.data.local.database.toRecentProduct
import woowacourse.shopping.data.model.product.Product
import woowacourse.shopping.data.model.product.toRecentProductEntity
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.utils.getFixtureRecentProducts
import java.time.LocalDateTime

class FakeRecentProductRepository : RecentProductRepository {
    private var recentProducts = getFixtureRecentProducts(10)

    override suspend fun save(product: Product) {
        if (findOrNullByProductId(product.id) != null) {
            update(product.id)
        } else {
            val id = recentProducts.last().productId + 1
            recentProducts += RecentProduct(
                productId = id,
                productName = "apple$id",
                imageUrl = "image",
                dateTime = LocalDateTime.now(),
                category = "fashion"
            )
        }
    }

    override suspend fun update(productId: Int) {
        val targetProduct = recentProducts.first { it.productId == productId }
        recentProducts =
            recentProducts.filter { it.productId != productId } +
                    targetProduct.copy(dateTime = LocalDateTime.now())
    }

    override suspend fun findOrNullByProductId(productId: Int): RecentProduct? {
        return recentProducts.firstOrNull { it.productId == productId }
    }

    override suspend fun findMostRecentProduct(): RecentProduct? {
        return recentProducts.lastOrNull()
    }

    override suspend fun findAll(limit: Int): List<RecentProduct> {
        return recentProducts
    }

    override suspend fun deleteAll() {
        recentProducts = emptyList()
    }
}
