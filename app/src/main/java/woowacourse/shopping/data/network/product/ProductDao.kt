package woowacourse.shopping.data.network.product

import woowacourse.shopping.domain.Product

interface ProductDao {

    suspend fun findAllProduct(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
    ): List<Product>

    suspend fun findById(id: String): Product
}
