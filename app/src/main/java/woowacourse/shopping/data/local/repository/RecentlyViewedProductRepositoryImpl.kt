package woowacourse.shopping.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import woowacourse.shopping.data.local.dao.RecentlyViewedProductDao
import woowacourse.shopping.data.local.entity.RecentlyViewedProductEntity
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.RecentlyViewedProductRepository

class RecentlyViewedProductRepositoryImpl(
    private val recentlyViewedProductDao: RecentlyViewedProductDao,
) : RecentlyViewedProductRepository {
    override fun getAll(): Flow<List<Long>?> = recentlyViewedProductDao.getAll().map { entities ->
        entities?.map { it.id }
    }

    override suspend fun updateList(product: Product) {
        recentlyViewedProductDao.enqueueAndLimit10(RecentlyViewedProductEntity(product.id))
    }

    override fun getLatestItem(): Flow<Long?> = recentlyViewedProductDao.getLatestItemId()
}
