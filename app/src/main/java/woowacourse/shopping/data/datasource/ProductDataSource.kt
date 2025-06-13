package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.response.ProductResponse
import woowacourse.shopping.data.model.response.ProductsResponse

interface ProductDataSource {
    suspend fun fetchProduct(id: Long): Result<ProductResponse>

    suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): Result<ProductsResponse>
}
