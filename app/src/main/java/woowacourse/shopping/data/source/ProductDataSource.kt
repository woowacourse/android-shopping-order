package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.ProductData

interface ProductDataSource {
    fun findByPaged(page: Int): List<ProductData>

    fun findAllUntilPage(page: Int): List<ProductData>

    fun findById(id: Long): ProductData

    fun findByCategory(category: String): List<ProductData>

    fun isFinalPage(page: Int): Boolean

    fun shutDown(): Boolean

    suspend fun findByPaged2(page: Int): Result<List<ProductData>>

    suspend fun findAllUntilPage2(page: Int): Result<List<ProductData>>

    suspend fun findById2(id: Long): Result<ProductData>

    suspend fun findByCategory2(category: String): Result<List<ProductData>>

    suspend fun isFinalPage2(page: Int): Result<Boolean>
}
