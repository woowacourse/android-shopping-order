package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.entity.Product

interface ShoppingRepository {
    suspend fun products(
        currentPage: Int,
        size: Int,
    ): Result<List<Product>>

    fun products(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<List<Product>>

    fun productById(id: Long): Result<Product>

    fun canLoadMore(
        page: Int,
        size: Int,
    ): Result<Boolean>

    suspend fun recentProducts(size: Int): Result<List<Product>>

    fun saveRecentProduct(id: Long): Result<Long>
}
