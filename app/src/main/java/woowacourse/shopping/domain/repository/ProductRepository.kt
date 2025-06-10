package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.model.Products

interface ProductRepository {
    suspend fun fetchProducts(
        page: Int,
        size: Int,
        category: String? = null,
    ): Products

    suspend fun fetchAllProducts(): List<Product>

    suspend fun fetchProduct(productId: Long): ProductDetail
}
