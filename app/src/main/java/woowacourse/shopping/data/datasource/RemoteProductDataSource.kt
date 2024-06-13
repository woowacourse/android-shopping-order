package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.product.Product
import woowacourse.shopping.data.model.product.ProductResponse

interface RemoteProductDataSource {
    suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): ProductResponse

    suspend fun getProductById(id: Int): Product

    suspend fun getRecommendedProducts(
        category: String?,
        maxSize: Int,
        sort: String,
    ): ProductResponse
}
