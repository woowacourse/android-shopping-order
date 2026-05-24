package woowacourse.shopping.data.local.repository

import woowacourse.shopping.data.local.dao.OutstandingProductDao
import woowacourse.shopping.data.local.entity.OutstandingProductEntity

class OutstandingProductRepositoryImpl(
    private val outstandingProductDao: OutstandingProductDao,
) : OutstandingProductRepository {
    override suspend fun getAll(): List<Long> {
        val items = outstandingProductDao.getAll()
        return items.map { it.cartItemId }
    }

    override suspend fun insertAll(cartItemIds: List<Long>) {
        val entities = cartItemIds.map { OutstandingProductEntity(it) }
        outstandingProductDao.insertAll(entities)
    }

    override suspend fun deleteAll() {
        outstandingProductDao.deleteAll()
    }
}
