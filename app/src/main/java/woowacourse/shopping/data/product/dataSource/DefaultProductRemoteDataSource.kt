package woowacourse.shopping.data.product.dataSource

import woowacourse.shopping.data.product.remote.dto.ProductResponseDto
import woowacourse.shopping.data.product.remote.dto.ProductsResponseDto
import woowacourse.shopping.data.product.remote.service.ProductService

class DefaultProductRemoteDataSource(
    private val productService: ProductService,
) : ProductRemoteDataSource {
    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): ProductsResponseDto = productService.getProducts(page, size)

    override suspend fun getProductDetail(productId: Long): ProductResponseDto = productService.getProductDetail(productId)
}
