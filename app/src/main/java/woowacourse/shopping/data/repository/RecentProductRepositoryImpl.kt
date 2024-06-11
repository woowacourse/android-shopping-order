package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.data.local.room.recentproduct.RecentProductDao
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.result.Fail
import woowacourse.shopping.domain.result.Result
import java.time.LocalDateTime

class RecentProductRepositoryImpl private constructor(private val recentProductDao: RecentProductDao) :
    RecentProductRepository {

        override suspend fun insert(productId: Long): Result<Long> {
            return try {
                val productId =
                    recentProductDao.insert(
                        RecentProduct(
                            productId = productId,
                            recentTime = LocalDateTime.now(),
                        ),
                    )
                Result.Success(productId)
            } catch (e: Exception) {
                Result.Exception(e)
            }
        }


        override suspend fun getMostRecentProduct(): Result<RecentProduct> {
            return try {
                val result =
                    recentProductDao.findMostRecentProduct() ?: return Fail.NotFound("최근 상품이 없습니다.")
                Result.Success(result)
            } catch (e: Exception) {
                Result.Exception(e)
            }
        }

        override suspend fun getAllRecentProducts(): Result<List<RecentProduct>> {
            return try {
                Result.Success(recentProductDao.findAll())
            } catch (e: Exception) {
                Result.Exception(e)
            }
        }

        override suspend fun deleteAll(): Result<Unit> {
            return try {
                Result.Success(recentProductDao.deleteAll())
            } catch (e: Exception) {
                Result.Exception(e)
            }
        }

        companion object {
            private var instance: RecentProductRepository? = null

            fun get(recentProductDao: RecentProductDao): RecentProductRepository {
                return instance ?: synchronized(this) {
                    instance ?: RecentProductRepositoryImpl(recentProductDao).also { instance = it }
                }
            }
        }
    }
