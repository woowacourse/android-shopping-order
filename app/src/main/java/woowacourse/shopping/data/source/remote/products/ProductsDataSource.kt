package woowacourse.shopping.data.source.remote.products

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.ProductsResponse

interface ProductsDataSource {
    suspend fun getProducts(
        page: Int?,
        size: Int?,
    ): Result<ProductsResponse>

    suspend fun getProductById(id: Long): Result<ProductResponse>

    suspend fun getProductsByCategory(category: String): Result<ProductsResponse>
}
