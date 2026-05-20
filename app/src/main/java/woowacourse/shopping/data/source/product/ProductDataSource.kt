package woowacourse.shopping.data.source.product

import woowacourse.shopping.domain.Product

interface ProductDataSource {
    suspend fun loadProducts(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): Pair<List<Product>, Boolean>

    suspend fun getProduct(id: Long): Product
}
