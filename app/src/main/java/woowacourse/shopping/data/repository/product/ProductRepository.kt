package woowacourse.shopping.data.repository.product

import woowacourse.shopping.domain.Product

interface ProductRepository {
    suspend fun loadProducts(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): List<Product>

    suspend fun getProduct(id: String): Product
}
