package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.product.ProductContent
import woowacourse.shopping.data.dto.product.ProductResponse
import woowacourse.shopping.data.service.ProductService

class CatalogRemoteDataSourceImpl(
    private val productService: ProductService,
) : CatalogRemoteDataSource {
    override suspend fun fetchProducts(
        category: String?,
        page: Int,
        size: Int,
    ): ProductResponse = productService.requestProducts(category = category, page = page, size = size)

    override suspend fun fetchAllProducts(): ProductResponse = productService.requestProducts()

    override suspend fun fetchProductDetail(id: Long): ProductContent = productService.requestDetailProduct(id)
}
