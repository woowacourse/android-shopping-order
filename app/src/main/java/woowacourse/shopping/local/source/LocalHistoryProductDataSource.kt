package woowacourse.shopping.local.source

import android.util.Log
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

    override fun loadLatestProduct(): HistoryProduct =
        dao.findLatest()
            ?: throw NoSuchElementException("No such element found in history product table for latest product.")

    override fun loadAllProductHistory(): List<HistoryProduct> = dao.findAll()

    override suspend fun saveProductHistory2(productId: Long): Result<Unit> =
        runCatching {
            dao.insert2(HistoryProduct(productId))
        }

    override suspend fun loadLatestProduct2(): Result<HistoryProduct> =
        runCatching {
            val result = dao.findLatest2() ?: return Result.failure(Exception("No latest product found"))
            result
        }.also {
            Log.d(TAG, "loadLatestProduct2: $it")
        }

    override suspend fun loadRecentProducts(size: Int): Result<List<HistoryProduct>> =
        runCatching {
            dao.findAll2(size)
        }

    companion object {
        private const val TAG = "LocalHistoryProductDataSource"
        private const val EMPTY = -1L
    }
}
