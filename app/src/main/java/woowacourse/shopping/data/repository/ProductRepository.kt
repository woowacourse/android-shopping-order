package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Product

interface ProductRepository {
    suspend fun getProducts(
        category: String = "",
        page: Int,
        size: Int,
    ): Result<ProductResponseResult>

    suspend fun getProductById(id: Long): Result<Product>
}
