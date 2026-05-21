package woowacourse.shopping.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        recentlyViewedProductDao.insert(RecentlyViewedProductEntity(id = product.id))

        val currentItems = recentlyViewedProductDao.getAll().first()
        if (currentItems?.let { it.size > MAX_RECENT_COUNT } == true) {
            val oldestItem = currentItems.last()
            recentlyViewedProductDao.deleteById(oldestItem.id)
        }
    }

    override fun getLatestItem(): Flow<Long?> = recentlyViewedProductDao.getLatestItemId()

    companion object {
        private const val MAX_RECENT_COUNT = 10
    }
}
