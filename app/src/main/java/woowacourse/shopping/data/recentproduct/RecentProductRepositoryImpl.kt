package woowacourse.shopping.data.recentproduct

import java.time.LocalDateTime
import kotlin.concurrent.thread

class RecentProductRepositoryImpl private constructor(private val recentProductDao: RecentProductDao) :
    RecentProductRepository {
        override fun insert(productId: Long): Result<Long> =
            runCatching {
                var id = -1L
                thread {
                    id =
                        recentProductDao.insert(
                            RecentProduct(
                                productId = productId,
                                recentTime = LocalDateTime.now(),
                            ),
                        )
                }.join()
                id
            }

        override fun findMostRecentProduct(): Result<RecentProduct> =
            runCatching {
                var recentProduct: RecentProduct? = null
                thread {
                    recentProduct = recentProductDao.findMostRecentProduct()
                }.join()
                recentProduct ?: error("최근 본 상품 정보를 불러오지 못했습니다")
            }

        override fun findAll(): Result<List<RecentProduct>> =
            runCatching {
                var recentProducts = emptyList<RecentProduct>()
                thread {
                    recentProducts = recentProductDao.findAll()
                }.join()
                recentProducts
            }

        override fun deleteAll(): Result<Unit> =
            runCatching {
                thread {
                    recentProductDao.deleteAll()
                }.join()
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
