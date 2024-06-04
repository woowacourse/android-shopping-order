package woowacourse.shopping.data.source

import woowacourse.shopping.domain.model.Product

interface ProductDataSource {
    suspend fun loadProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>>

    suspend fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Result<List<Product>>

    suspend fun loadProduct(id: Int): Result<Product>
}
