package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.entity.Product

interface ProductRepository {
    fun loadProducts(
        currentPage: Int,
        size: Int,
    ): Result<List<Product>>

    fun loadProducts(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<List<Product>>

    fun findProductById(id: Long): Result<Product>

    fun canLoadMore(
        page: Int,
        size: Int,
    ): Result<Boolean>

    fun loadRecentProducts(size: Int): Result<List<Product>>

    fun saveRecentProduct(id: Long): Result<Long>
}
