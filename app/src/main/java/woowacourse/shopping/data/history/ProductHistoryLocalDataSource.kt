package woowacourse.shopping.data.history

import woowacourse.shopping.data.model.HistoryProduct
import woowacourse.shopping.local.history.HistoryProductDao

class ProductHistoryLocalDataSource(private val dao: HistoryProductDao) : ProductHistoryDataSource {
    override fun saveProductHistory(productId: Long) {
        val id = dao.findById(productId)

        if (id != null) {
            dao.delete(id)
        }

        dao.insert(HistoryProduct(productId))
    }

    override fun fetchProductHistory(productId: Long): Long? = dao.findById(productId)?.id

    override fun fetchLatestProduct(): Long = dao.findLatest()?.id ?: EMPTY

    override fun fetchProductsHistory(): List<Long> = dao.findAll().map { it.id }

    override fun deleteProductsHistory() {
        dao.deleteAll()
    }

    companion object {
        private const val EMPTY = -1L
    }
}
