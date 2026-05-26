package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.query.ProductPageResult

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
