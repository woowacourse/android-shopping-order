package woowacourse.shopping.data.local.datasource

import woowacourse.shopping.data.datasource.RecentDataSource
import woowacourse.shopping.data.local.dao.RecentProductDao
import woowacourse.shopping.data.local.entity.RecentProductEntity

class LocalRecentDataSource(private val dao: RecentProductDao) : RecentDataSource {
    override suspend fun save(recentProductEntity: RecentProductEntity): Result<Unit> {
        return runCatching { dao.save(recentProductEntity) }
    }

    override suspend fun update(
        productId: Int,
        dateTime: String,
    ): Result<Unit> {
        return runCatching { dao.update(productId, dateTime) }
    }

    override suspend fun findByProductId(productId: Int): Result<RecentProductEntity?> {
        return runCatching { dao.findByProductId(productId) }
    }

    override suspend fun findMostRecentProduct(): Result<RecentProductEntity?> {
        return runCatching { dao.findMostRecentProduct() }
    }

    override suspend fun findAll(limit: Int): Result<List<RecentProductEntity>> {
        return runCatching { dao.findAll(limit) }
    }

    override suspend fun deleteAll(): Result<Unit> {
        return runCatching { dao.deleteAll() }
    }
}
