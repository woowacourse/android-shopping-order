package woowacourse.shopping.local.source

import android.util.Log
import woowacourse.shopping.data.model.HistoryProduct
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.shopping.local.history.HistoryProductDao

class LocalHistoryProductDataSource(private val dao: HistoryProductDao) : ProductHistoryDataSource {
    override suspend fun saveProductHistory(productId: Long): Result<Unit> =
        runCatching {
            dao.insert2(HistoryProduct(productId))
        }

    override suspend fun loadLatestProduct(): Result<HistoryProduct> =
        runCatching {
            val result = dao.findLatest2() ?: return Result.failure(Exception("No latest product found"))
            result
        }.also {
            Log.d(TAG, "loadLatestProduct2: $it")
        }

    override suspend fun loadRecentProduct(size: Int): Result<List<HistoryProduct>> =
        runCatching {
            dao.findAll2(size)
        }

    companion object {
        private const val TAG = "LocalHistoryProductDataSource"
        private const val EMPTY = -1L
    }
}
