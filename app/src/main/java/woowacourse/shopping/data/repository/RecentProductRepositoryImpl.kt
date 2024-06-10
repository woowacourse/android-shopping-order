package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import woowacourse.shopping.data.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class RecentProductRepositoryImpl private constructor(private val recentProductLocalDataSource: RecentProductLocalDataSource) :
    RecentProductRepository {
        override suspend fun insert(productId: Long): Result<Long> =
            recentProductLocalDataSource.insert(
                RecentProduct(
                    productId = productId,
                    recentTime = LocalDateTime.now(),
                ),
            )

        override suspend fun findMostRecentProduct(): Result<RecentProduct> = recentProductLocalDataSource.findMostRecentProduct()

        override suspend fun findAll(): Result<List<RecentProduct>> = recentProductLocalDataSource.findAll()

        override suspend fun deleteAll(): Result<Unit> = recentProductLocalDataSource.deleteAll()

        companion object {
            private var instance: RecentProductRepository? = null

            fun get(recentProductLocalDataSource: RecentProductLocalDataSource): RecentProductRepository {
                return instance ?: synchronized(this) {
                    instance ?: RecentProductRepositoryImpl(recentProductLocalDataSource).also {
                        instance = it
                    }
                }
            }
        }
    }
