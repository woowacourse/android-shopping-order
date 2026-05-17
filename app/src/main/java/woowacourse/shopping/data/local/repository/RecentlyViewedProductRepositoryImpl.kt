package woowacourse.shopping.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import woowacourse.shopping.data.local.dao.RecentlyViewedProductDao
import woowacourse.shopping.data.local.entity.RecentlyViewedProductEntity
import woowacourse.shopping.domain.Product

class RecentlyViewedProductRepositoryImpl(
    private val recentlyViewedProductDao: RecentlyViewedProductDao,
) : RecentlyViewedProductRepository {
    override fun getAll(): Flow<List<Long>?> {
        val entities = recentlyViewedProductDao.getAll()
        return entities.map { productEntities ->
            productEntities?.map {
                it.id
            }
        }
    }

    override suspend fun updateList(product: Product) {
        recentlyViewedProductDao.enqueueAndLimit10(RecentlyViewedProductEntity(product.id))
    }

    override fun getLatestItem(): Flow<Long?> = recentlyViewedProductDao.getLatestItemId()
}
