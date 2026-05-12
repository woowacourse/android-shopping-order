package woowacourse.shopping.data.source.product

import woowacourse.shopping.domain.Product

interface ProductDataSource {
    suspend fun loadProducts(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
    ): List<Product>

    suspend fun getProduct(id: String): Product
}
