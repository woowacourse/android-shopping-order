package woowacourse.shopping.data.source.remote

import retrofit2.Retrofit
import woowacourse.shopping.data.source.remote.api.ProductService
import woowacourse.shopping.data.source.remote.dto.product.ProductContent
import woowacourse.shopping.data.source.remote.dto.product.ProductResponse
import kotlin.jvm.java

sealed interface FetchResult {
    data class Success(
        val products: List<ProductContent>,
    ) : FetchResult

    object Failed : FetchResult
}

class ProductRemoteDataSource(
    retrofit: Retrofit,
) {
    private val productService = retrofit.create(ProductService::class.java)

    suspend fun fetchProducts(
        offset: Int,
        limit: Int,
    ): List<ProductContent> =
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

    suspend fun fetchProductById(id: Long): ProductResponse = productService.requestProduct(id = id)
}
