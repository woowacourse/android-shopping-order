package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.product.Product

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): List<Product>

    suspend fun getProduct(id: Long): Product

    suspend fun getCategoryProducts(
        page: Int = 0,
        pageSize: Int = 10,
        category: String,
    ): List<Product>
}
