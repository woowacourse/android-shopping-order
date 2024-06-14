package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.data.local.room.recentproduct.RecentProductDao
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result
import java.time.LocalDateTime

class RecentProductRepositoryImpl private constructor(private val recentProductDao: RecentProductDao) :
    RecentProductRepository {
        override suspend fun insert(productId: Long): Result<Long, DataError> {
            val productId =
                recentProductDao.insert(
                    RecentProduct(
                        productId = productId,
                        recentTime = LocalDateTime.now(),
                    ),
                )
            return Result.Success(productId)
        }

        override suspend fun getMostRecentProduct(): Result<RecentProduct, DataError> {
            val result =
                recentProductDao.findMostRecentProduct()
                    ?: return Result.Error(DataError.NotFound)
            return Result.Success(result)
        }

        override suspend fun getAllRecentProducts(): Result<List<RecentProduct>, DataError> = Result.Success(recentProductDao.findAll())

        override suspend fun deleteAll(): Result<Unit, DataError> = Result.Success(recentProductDao.deleteAll())

        companion object {
            private var instance: RecentProductRepository? = null

            fun get(recentProductDao: RecentProductDao): RecentProductRepository {
                return instance ?: synchronized(this) {
                    instance ?: RecentProductRepositoryImpl(recentProductDao).also { instance = it }
                }
            }
        }
    }
