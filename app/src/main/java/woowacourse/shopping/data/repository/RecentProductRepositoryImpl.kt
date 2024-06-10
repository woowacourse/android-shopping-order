package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.datasource.LocalRecentDataSource
import woowacourse.shopping.data.local.entity.toRecentProduct
import woowacourse.shopping.data.local.entity.toRecentProducts
import woowacourse.shopping.data.mapper.toRecentProductEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class RecentProductRepositoryImpl(private val localRecentDataSource: LocalRecentDataSource) :
    RecentProductRepository {
    override suspend fun save(product: Product): Result<Unit> {
        return findOrNullByProductId(product.productId).mapCatching { recentProduct ->
            if (recentProduct != null) {
                update(product.productId)
            } else {
                localRecentDataSource.save(product.toRecentProductEntity())
            }
        }
    }

    override suspend fun update(productId: Int): Result<Unit> {
        return localRecentDataSource.update(productId, LocalDateTime.now().toString())
    }

    override suspend fun findOrNullByProductId(productId: Int): Result<RecentProduct?> {
        return localRecentDataSource.findByProductId(productId).mapCatching { recentProductEntity ->
            recentProductEntity?.toRecentProduct()
        }
    }

    override suspend fun findMostRecentProduct(): Result<RecentProduct?> {
        return localRecentDataSource.findMostRecentProduct().mapCatching { recentProductEntity ->
            recentProductEntity?.toRecentProduct()
        }
    }

    override suspend fun findAll(limit: Int): Result<List<RecentProduct>> {
        return localRecentDataSource.findAll(limit).mapCatching { recentProductEntities ->
            recentProductEntities.toRecentProducts()
        }
    }

    override suspend fun deleteAll(): Result<Unit> {
        return localRecentDataSource.deleteAll()
    }
}
