package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.product.ProductContent
import woowacourse.shopping.data.dto.product.ProductResponse

interface CatalogRemoteDataSource {
    suspend fun fetchProducts(
        category: String?,
        page: Int,
        size: Int,
    ): ProductResponse

    suspend fun fetchAllProducts(): ProductResponse

    suspend fun fetchProductDetail(id: Long): ProductContent
}
