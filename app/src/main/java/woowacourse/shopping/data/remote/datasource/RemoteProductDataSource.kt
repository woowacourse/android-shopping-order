package woowacourse.shopping.data.remote.datasource

import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductResponse
import woowacourse.shopping.data.remote.service.ProductService

class RemoteProductDataSource(private val productService: ProductService) : ProductDataSource {
    override suspend fun getProductResponse(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductResponse> {
        return runCatching {
            productService.getProducts(category, page, size, sort)
        }
    }

    override suspend fun getProductById(productId: Int): Result<ProductDto> {
        return runCatching {
            productService.getProductById(productId)
        }
    }
}
