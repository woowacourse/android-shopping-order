package woowacourse.shopping.data.source.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.source.remote.api.ProductService
import woowacourse.shopping.data.source.remote.dto.product.ProductContent
import woowacourse.shopping.data.source.remote.dto.product.ProductResponse

class ProductRemoteDataSource(
    private val productService: ProductService,
) {
    suspend fun fetchProducts(
        offset: Int,
        limit: Int,
    ): List<ProductContent> {
        return try {
            val response =
                productService.requestProducts(
                    page = offset,
                    size = limit,
                )
            response.content
        } catch (_: Exception) {
            emptyList()
        }
    }
}

