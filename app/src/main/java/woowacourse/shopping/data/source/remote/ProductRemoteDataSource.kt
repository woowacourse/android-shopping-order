package woowacourse.shopping.data.source.remote

import woowacourse.shopping.data.source.remote.api.ProductService
import woowacourse.shopping.data.source.remote.dto.product.ProductContent

class ProductRemoteDataSource(
    private val productService: ProductService,
) {
    suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): List<ProductContent> =
        try {
            val response =
                productService.requestProducts(
                    page = page,
                    size = size,
                )
            response.content
        } catch (_: Exception) {
            emptyList()
        }
}
