package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.response.product.ProductResponse
import woowacourse.shopping.data.model.response.product.ProductsResponse
import woowacourse.shopping.data.service.ProductService

class ProductDataSourceImpl(
    private val productService: ProductService,
) : ProductDataSource {
    override suspend fun fetchProduct(id: Long): ProductResponse {
        return productService.getProduct(id)
    }

    override suspend fun fetchPageOfProducts(
        pageIndex: Int,
        pageSize: Int,
    ): ProductsResponse {
        return productService.getProducts(page = pageIndex, size = pageSize)
    }
}
