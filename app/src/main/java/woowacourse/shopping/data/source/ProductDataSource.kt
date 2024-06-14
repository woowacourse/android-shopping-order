package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.ProductData

interface ProductDataSource {
    suspend fun findByPaged(page: Int): Result<List<ProductData>>

    suspend fun findAllUntilPage(page: Int): Result<List<ProductData>>

    suspend fun findById(id: Long): Result<ProductData>

    suspend fun findByCategory(category: String): Result<List<ProductData>>

    suspend fun isFinalPage(page: Int): Result<Boolean>

    fun shutDown(): Boolean
}
