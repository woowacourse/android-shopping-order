package woowacourse.shopping.local.source

import woowacourse.shopping.data.model.HistoryProduct
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.shopping.local.history.HistoryProductDao

class LocalHistoryProductDataSource(private val dao: HistoryProductDao) : ProductHistoryDataSource {
    override fun saveProductHistory(productId: Long) {
        val id = dao.findById(productId)

        if (id != null) {
            dao.delete(id)
        }

        dao.insert(HistoryProduct(productId))
    }

    override fun loadLatestProduct(): Long = dao.findLatest()?.id ?: EMPTY

    override fun loadAllProductHistory(): List<Long> = dao.findAll().map { it.id }

    companion object {
        private const val EMPTY = -1L
    }
}
