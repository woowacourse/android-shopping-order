package woowacourse.shopping.data.recent

import woowacourse.shopping.data.recent.RecentProductEntity.Companion.toEntity
import woowacourse.shopping.domain.RecentProductItem
import woowacourse.shopping.domain.repository.RecentRepository

class RecentProductRepositoryImpl(private val dao: RecentProductDao) : RecentRepository {
    override suspend fun loadAll(): Result<List<RecentProductItem>> {
        return runCatching {
            dao.loadAll().map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun loadMostRecent(): Result<RecentProductItem?> {
        return runCatching {
            val entity = dao.getMostRecent()
            entity?.toDomain()
        }
    }

    override suspend fun add(recentProduct: RecentProductItem): Result<Long> {
        return runCatching {
            dao.insert(
                recentProduct.toEntity(),
            )
        }
    }
}
