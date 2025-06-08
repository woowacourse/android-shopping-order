package woowacourse.shopping.data.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.model.response.ProductResponse
import woowacourse.shopping.data.model.response.ProductsResponse
import woowacourse.shopping.data.service.ProductService

class ProductDataSourceImpl(
    private val productService: ProductService,
) : ProductDataSource {
    override suspend fun fetchProduct(id: Long):ProductResponse {
        return productService.getProduct(id)
    }

    override suspend fun fetchPageOfProducts(
        pageIndex: Int,
        pageSize: Int,
    ): ProductsResponse {
        return productService.getProducts(page = pageIndex, size = pageSize)
    }
}
