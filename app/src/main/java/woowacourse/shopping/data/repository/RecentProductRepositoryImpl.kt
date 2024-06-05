package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.recent.RecentProductDatabase
import woowacourse.shopping.data.local.recent.RecentProductEntity
import woowacourse.shopping.data.local.recent.toRecentProduct
import woowacourse.shopping.data.mapper.toRecentProductEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime
import kotlin.concurrent.thread

class RecentProductRepositoryImpl(recentProductDatabase: RecentProductDatabase) :
    RecentProductRepository {
    private val dao = recentProductDatabase.recentProductDao()

    override fun save(product: Product) {
        if (findOrNullByProductId(product.productId) != null) {
            update(product.productId)
        } else {
            thread {
                dao.save(product.toRecentProductEntity())
            }.join()
        }
    }

    override fun update(productId: Int) {
        thread {
            dao.update(productId, LocalDateTime.now().toString())
        }.join()
    }

    override fun findOrNullByProductId(productId: Int): RecentProduct? {
        var recentProductEntity: RecentProductEntity? = null
        thread {
            recentProductEntity = dao.findByProductId(productId)
        }.join()
        return recentProductEntity?.toRecentProduct()
    }

    override fun findMostRecentProduct(): RecentProduct? {
        var recentProduct: RecentProductEntity? = null
        thread {
            recentProduct = dao.findMostRecentProduct()
        }.join()
        return recentProduct?.toRecentProduct()
    }

    override fun findAll(limit: Int): List<RecentProduct> {
        var recentProducts: List<RecentProduct> = emptyList()
        thread {
            recentProducts = dao.findAll(limit).map { it.toRecentProduct() }
        }.join()
        return recentProducts
    }

    override fun deleteAll() {
        thread {
            dao.deleteAll()
        }.join()
    }
}
