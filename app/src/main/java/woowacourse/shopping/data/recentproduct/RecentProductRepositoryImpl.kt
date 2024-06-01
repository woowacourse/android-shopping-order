package woowacourse.shopping.data.recentproduct

import woowacourse.shopping.data.datasource.RecentProductLocalDataSource
import java.time.LocalDateTime

class RecentProductRepositoryImpl private constructor(private val recentProductLocalDataSource: RecentProductLocalDataSource) :
    RecentProductRepository {
        override fun insert(productId: Long): Result<Long> =
            recentProductLocalDataSource.insert(
                RecentProduct(
                    productId = productId,
                    recentTime = LocalDateTime.now(),
                ),
            )

        override fun findMostRecentProduct(): Result<RecentProduct> = recentProductLocalDataSource.findMostRecentProduct()

        override fun findAll(): Result<List<RecentProduct>> = recentProductLocalDataSource.findAll()

        override fun deleteAll(): Result<Unit> = recentProductLocalDataSource.deleteAll()

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
