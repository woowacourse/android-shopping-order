package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun loadWithCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
    ): Result<List<Product>>

    suspend fun load(
        startPage: Int,
        pageSize: Int,
    ): Result<List<Product>>

    suspend fun loadById(id: Long): Result<Product>
}
