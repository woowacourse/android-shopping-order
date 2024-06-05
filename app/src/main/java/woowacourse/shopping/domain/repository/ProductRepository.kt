package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.product.Product

interface ProductRepository {
    suspend fun loadPagingProducts(offset: Int): Result<List<Product>>

    suspend fun loadCategoryProducts(
        size: Int,
        category: String,
    ): Result<List<Product>>

    suspend fun getProduct(productId: Long): Result<Product>
}
