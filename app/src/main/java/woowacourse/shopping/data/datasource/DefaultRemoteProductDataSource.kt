package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.remote.ProductService

class DefaultRemoteProductDataSource(private val productService: ProductService) :
    RemoteProductDataSource {
    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): ProductResponse {
        return productService.getProducts(category, page, size, sort)
    }

    override suspend fun getProductById(id: Int): Product {
        return productService.getProductById(id)
    }
}
