package woowacourse.shopping.data.local.history.repository

import woowacourse.shopping.domain.model.Product

interface HistoryRepository {
    suspend fun getAll(): List<Product>

    suspend fun insert(product: Product)

    suspend fun findLatest(): Product?
}
