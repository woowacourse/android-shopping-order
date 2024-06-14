package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.entity.Product

interface ShoppingRepository {
    suspend fun products(
        currentPage: Int,
        size: Int,
    ): Result<List<Product>>

    suspend fun products(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<List<Product>>

    suspend fun productById(id: Long): Result<Product>

    fun canLoadMore(
        page: Int,
        size: Int,
    ): Result<Boolean>

    suspend fun recentProducts(size: Int): Result<List<Product>>

    suspend fun saveRecentProduct(id: Long): Result<Long>
}
