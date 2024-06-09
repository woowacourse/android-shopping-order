package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.ProductData

interface ProductDataSource {
    suspend fun findByPaged2(page: Int): Result<List<ProductData>>

    suspend fun findAllUntilPage2(page: Int): Result<List<ProductData>>

    suspend fun findById2(id: Long): Result<ProductData>

    suspend fun findByCategory2(category: String): Result<List<ProductData>>

    suspend fun isFinalPage2(page: Int): Result<Boolean>

    fun shutDown(): Boolean
}
