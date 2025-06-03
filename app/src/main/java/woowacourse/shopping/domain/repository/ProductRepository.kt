package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products

interface ProductRepository {
    suspend fun fetchProducts(
        page: Int,
        size: Int,
        category: String? = null,
    ): Result<Products>

    suspend fun fetchAllProducts(): Result<List<Product>>
}
