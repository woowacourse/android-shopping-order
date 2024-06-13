package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.local.LocalRecentViewedDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.local.entity.RecentProductEntity.Companion.toRecentProductEntity
import java.time.LocalDateTime

class RecentProductRepositoryImpl(private val localRecentViewedDataSource: LocalRecentViewedDataSource) : RecentRepository {
    override suspend fun loadAll(): Result<List<Product>> {
        return runCatching {
            localRecentViewedDataSource.loadAll().map { it.toDomain() }
        }
    }

    override suspend fun loadMostRecent(): Result<Product?> {
        return runCatching {
            localRecentViewedDataSource.getMostRecent()?.toDomain()
        }
    }

    override suspend fun add(recentProduct: Product): Result<Unit> {
        return runCatching {
            localRecentViewedDataSource.save(
                recentProduct.toRecentProductEntity(LocalDateTime.now()),
            )
        }
    }
}
