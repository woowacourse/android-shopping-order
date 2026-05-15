package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.domain.Product

interface ProductDataSource {

    suspend fun findAllProduct(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): List<Product>

    suspend fun findById(id: String): Product
}
