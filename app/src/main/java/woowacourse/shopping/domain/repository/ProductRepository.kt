package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductsPage

interface ProductRepository {
    suspend fun getProducts(
        offset: Int,
        limit: Int,
        category: String? = null,
    ): ProductsPage

    suspend fun getProductById(id: Long): Product
}
