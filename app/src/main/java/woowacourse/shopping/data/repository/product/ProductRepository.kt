package woowacourse.shopping.data.repository.product

import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ProductPage

interface ProductRepository {
    suspend fun loadProducts(
        page: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): ProductPage

    suspend fun getProduct(id: String): Product
}
