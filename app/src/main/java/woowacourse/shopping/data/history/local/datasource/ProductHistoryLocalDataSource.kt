package woowacourse.shopping.data.history.local.datasource

import woowacourse.shopping.data.history.local.HistoryProductDao
import woowacourse.shopping.data.model.HistoryProduct

class ProductHistoryLocalDataSource(private val dao: HistoryProductDao) : ProductHistoryDataSource {
    override suspend fun saveProductHistory(productId: Long) {
        val id = dao.findById(productId)

        if (id != null) {
            dao.delete(id)
        }

        dao.insert(HistoryProduct(productId))
    }

    override suspend fun fetchProductHistory(productId: Long): Long? = dao.findById(productId)?.id

    override suspend fun fetchLatestProduct(): Long = dao.findLatest()?.id ?: EMPTY

    override suspend fun fetchProductsHistory(): List<Long> = dao.findAll().map { it.id }

    override suspend fun deleteProductsHistory() {
        dao.deleteAll()
    }

    companion object {
        private const val EMPTY = -1L
    }
}
