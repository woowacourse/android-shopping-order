package woowacourse.shopping.data.history.local.datasource

import woowacourse.shopping.data.history.local.HistoryProductDao
import woowacourse.shopping.data.model.HistoryProduct

class ProductHistoryLocalDataSource(private val dao: HistoryProductDao) : ProductHistoryDataSource {
    override suspend fun saveProductHistory(productId: Long): Result<Long> {
        val id = dao.findById(productId)
        if (id != null) { dao.delete(id) }

        return runCatching {
            dao.insert(HistoryProduct(productId))
        }.onSuccess {
            Result.success(it)
        }.onFailure {
            Result.failure<Long>(it)
        }
    }

    override suspend fun fetchProductHistory(productId: Long): Result<Long?> =
        runCatching {
            dao.findById(productId)?.id
        }.onSuccess { id ->
            Result.success(id)
        }.onFailure {
            Result.failure<Long>(it)
        }

    override suspend fun fetchLatestProduct(): Result<Long> =
        runCatching {
            dao.findLatest()?.id ?: EMPTY
        }.onSuccess { id ->
            Result.success(id)
        }.onFailure {
            Result.failure<Long>(it)
        }

    override suspend fun fetchProductsHistory(): Result<List<Long>> =
        runCatching {
            dao.findAll().map { it.id }
        }.onSuccess { productIds ->
            Result.success(productIds)
        }.onFailure {
            Result.failure<List<Long>>(it)
        }

    override suspend fun deleteProductsHistory() {
        dao.deleteAll()
    }

    companion object {
        private const val EMPTY = -1L
    }
}
