package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductDomain2

interface ProductRepository {
    suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductDomain2>

    suspend fun getProductById(id: Int): Result<OrderableProduct>

    suspend fun getRecommendedProducts(): Result<List<OrderableProduct>>
}
