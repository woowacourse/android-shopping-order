package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.network.response.products.ProductResponse
import woowacourse.shopping.data.network.response.products.ProductsResponse
import woowacourse.shopping.data.network.service.ProductService

class ProductsDataSource(private val service: ProductService) {
    suspend fun singlePage(
        category: String?,
        page: Int?,
        size: Int?,
    ): ProductsResponse {
        return service.requestProducts(category, page, size)
    }

    suspend fun getProduct(productId: Long): ProductResponse {
        return service.getProduct(productId)
    }
}
