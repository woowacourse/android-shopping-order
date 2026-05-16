package woowacourse.shopping.data.source.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.source.remote.api.ProductService
import woowacourse.shopping.data.source.remote.dto.product.ProductContent
import woowacourse.shopping.data.source.remote.dto.product.ProductResponse

sealed interface FetchResult {
    data class Success(
        val products: List<ProductContent>,
    ) : FetchResult

    object Failed : FetchResult
}

class ProductRemoteDataSource(
    private val productService: ProductService,
) {
    suspend fun fetchProducts(
        offset: Int,
        limit: Int,
    ): List<ProductContent> =
        withContext(Dispatchers.IO) {
            try {
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

    suspend fun fetchProductById(id: Long): ProductResponse =
        withContext(Dispatchers.IO) {
            productService.requestProduct(id = id)
        }
}
