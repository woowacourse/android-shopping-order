package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.data.local.room.recentproduct.RecentProductDao
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.response.Fail
import woowacourse.shopping.domain.response.Response
import java.time.LocalDateTime

class RecentProductRepositoryImpl private constructor(private val recentProductDao: RecentProductDao) :
    RecentProductRepository {
    override suspend fun insert(productId: Long): Long =
        recentProductDao.insert(
            RecentProduct(
                productId = productId,
                recentTime = LocalDateTime.now(),
            ),
        )

    override suspend fun insertResponse(productId: Long): Response<Long> {
        return try {
            val productId = recentProductDao.insert(
                RecentProduct(
                    productId = productId,
                    recentTime = LocalDateTime.now(),
                ),
            )
            Response.Success(productId)
        } catch (e: Exception) {
            Response.Exception(e)
        }
    }

    override suspend fun mostRecentProduct(): RecentProduct =
        recentProductDao.findMostRecentProduct() ?: throw NoSuchElementException()

    override suspend fun mostRecentProductOrNull(): RecentProduct? =
        recentProductDao.findMostRecentProduct()

    override suspend fun mostRecentProductResponse(): Response<RecentProduct> {
        return try {
            val result =
                recentProductDao.findMostRecentProduct() ?: return Fail.NotFound("최근 상품이 없습니다.")
            Response.Success(result)
        } catch (e: Exception) {
            Response.Exception(e)
        }
    }

    override suspend fun allRecentProducts(): List<RecentProduct> = recentProductDao.findAll()
    override suspend fun allRecentProductsResponse(): Response<List<RecentProduct>> {
        return try {
            Response.Success(recentProductDao.findAll())
        } catch (e: Exception) {
            Response.Exception(e)
        }
    }

    override suspend fun deleteAll(): Response<Unit> {
        return try {
            Response.Success(recentProductDao.deleteAll())
        } catch (e: Exception) {
            Response.Exception(e)
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
