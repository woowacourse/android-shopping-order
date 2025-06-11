package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.model.Product

interface ProductsRepository {
    suspend fun getProducts(
        page: Int,
        size: Int = 20,
    ): Result<PagingData>

    suspend fun getProductById(id: Long): Result<Product>

    suspend fun getRecommendedProductsFromLastViewed(cartProductIds: List<Long>): Result<List<Product>>
}
