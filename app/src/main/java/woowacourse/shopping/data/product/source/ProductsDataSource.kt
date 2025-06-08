package woowacourse.shopping.data.product.source

import woowacourse.shopping.data.product.dto.ProductsResponse
import woowacourse.shopping.data.product.entity.ProductEntity

interface ProductsDataSource {
    suspend fun pagedProducts(
        page: Int,
        size: Int,
    ): ProductsResponse?

    suspend fun getProductById(id: Long): ProductEntity?

    suspend fun getProductsByCategory(category: String): List<ProductEntity>?
}
