package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.recent.RecentProductDatabase
import woowacourse.shopping.data.local.recent.RecentProductEntity
import woowacourse.shopping.data.local.recent.toRecentProduct
import woowacourse.shopping.data.mapper.toRecentProductEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class RecentProductRepositoryImpl(recentProductDatabase: RecentProductDatabase) :
    RecentProductRepository {
    private val dao = recentProductDatabase.recentProductDao()

    override fun save(product: Product) {
        if (findOrNullByProductId(product.productId) != null) {
            update(product.productId)
        } else {
            threadAction {
                dao.save(product.toRecentProductEntity())
            }
        }
    }

    override fun update(productId: Int) {
        threadAction {
            dao.update(productId, LocalDateTime.now().toString())
        }
    }

    override fun findOrNullByProductId(productId: Int): RecentProduct? {
        var recentProductEntity: RecentProductEntity? = null
        threadAction {
            recentProductEntity = dao.findByProductId(productId)
        }
        return recentProductEntity?.toRecentProduct()
    }

    override fun findMostRecentProduct(): RecentProduct? {
        var recentProduct: RecentProductEntity? = null
        threadAction {
            recentProduct = dao.findMostRecentProduct()
        }
        return recentProduct?.toRecentProduct()
    }

    override fun findAll(limit: Int): List<RecentProduct> {
        var recentProducts: List<RecentProduct> = emptyList()
        threadAction {
            recentProducts = dao.findAll(limit).map { it.toRecentProduct() }
        }
        return recentProducts
    }

    override fun deleteAll() {
        threadAction {
            dao.deleteAll()
        }
    }

    private fun threadAction(action: () -> Unit) {
        val thread = Thread(action)
        thread.start()
        thread.join()
    }
}
