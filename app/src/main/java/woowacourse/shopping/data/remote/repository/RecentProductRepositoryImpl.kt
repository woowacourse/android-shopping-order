package woowacourse.shopping.data.remote.repository

import woowacourse.shopping.data.local.RecentProductDataSource
import woowacourse.shopping.data.local.mapper.toDomain
import woowacourse.shopping.data.local.mapper.toEntity
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.RecentProductRepository

class RecentProductRepositoryImpl(
    private val recentProductDataSource: RecentProductDataSource,
) : RecentProductRepository {
    override suspend fun findAllByLimit(limit: Int): Result<List<RecentProduct>> =
        runCatching {
            recentProductDataSource.findAllByLimit(limit).map { it.toDomain() }
        }

    override suspend fun findOrNull(): Result<RecentProduct?> =
        runCatching {
            recentProductDataSource.findOrNull()?.toDomain()
        }

    override suspend fun save(recentProduct: RecentProduct): Result<Long> =
        runCatching {
            recentProductDataSource.save(recentProduct.toEntity())
        }
}
