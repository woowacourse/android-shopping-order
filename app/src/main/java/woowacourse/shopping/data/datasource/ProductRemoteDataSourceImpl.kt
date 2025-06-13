package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.model.product.ProductResponse
import woowacourse.shopping.data.service.ProductService

class ProductRemoteDataSourceImpl(
    private val productService: ProductService,
) : ProductRemoteDataSource {
    override suspend fun fetchProducts(
        category: String?,
        page: Int?,
        size: Int?,
    ): PageableResponse<ProductResponse> = productService.fetchProducts(category, page, size)

    override suspend fun fetchProduct(productId: Long): ProductResponse = productService.fetchProduct(productId)
}
