package woowacourse.shopping.repository

import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.query.ProductPageResult

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        size: Int,
    ): ProductPageResult

    suspend fun getProductsByCategory(
        category: String,
        page: Int,
        size: Int,
    ): ProductPageResult

    suspend fun findAllByIds(ids: Set<Long>): Map<Long, Product>
}
