package woowacourse.shopping.data.network.product

import woowacourse.shopping.domain.Product

interface ProductDao {
    suspend fun findAllProduct(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): Pair<List<Product>, Boolean>

    suspend fun findById(id: Long): Product
}
