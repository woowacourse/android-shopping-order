package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.entity.Product

interface ProductRepository {
    suspend fun loadProducts(
        currentPage: Int,
        size: Int,
    ): Result<List<Product>>

    suspend fun loadProducts(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<List<Product>>

    suspend fun findProductById(id: Long): Result<Product>

    fun canLoadMore(
        page: Int,
        size: Int,
    ): Result<Boolean>

    suspend fun loadRecentProducts(size: Int): Result<List<Product>>

    suspend fun saveRecentProduct(id: Long): Result<Long>
}
