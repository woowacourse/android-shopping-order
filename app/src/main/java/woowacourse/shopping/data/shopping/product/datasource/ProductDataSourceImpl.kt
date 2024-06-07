package woowacourse.shopping.data.shopping.product.datasource

import woowacourse.shopping.data.shopping.product.ProductPageData
import woowacourse.shopping.data.shopping.product.toData
import woowacourse.shopping.data.shopping.product.toProduct
import woowacourse.shopping.data.util.handleResponse
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.remote.service.ProductService

class ProductDataSourceImpl(
    private val productService: ProductService,
) : ProductDataSource {
    override suspend fun products(
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData> {
        val response = productService.fetchProducts(currentPage, size)
        val data = response.body()?.toData() ?: throw Exception("Empty body")
        return handleResponse(response, data)
    }

    override suspend fun products(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData> {
        val response = productService.fetchProducts(category, currentPage, size)
        val data = response.body()?.toData() ?: throw Exception("Empty body")
        return handleResponse(response, data)
    }

    override suspend fun fetchProductById(id: Long): Result<Product> {
        val response = productService.fetchDetailProduct(id)
        val product = response.body()?.toProduct() ?: throw Exception("Empty body")
        return handleResponse(response, product)
    }
}
