package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.response.product.ProductResponse
import woowacourse.shopping.data.model.response.product.ProductsResponse

interface ProductDataSource {
    suspend fun fetchProduct(id: Long): ProductResponse

    suspend fun fetchPageOfProducts(
        pageIndex: Int,
        pageSize: Int,
    ): ProductsResponse
}
