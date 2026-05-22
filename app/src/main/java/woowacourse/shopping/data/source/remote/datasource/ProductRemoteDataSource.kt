package woowacourse.shopping.data.source.remote.datasource

import retrofit2.Retrofit
import woowacourse.shopping.data.source.remote.api.ProductService
import woowacourse.shopping.data.source.remote.dto.product.ProductResponse
import woowacourse.shopping.data.source.remote.dto.product.ProductsResponse
import kotlin.jvm.java

class ProductRemoteDataSource(
    retrofit: Retrofit,
) {
    private val productService = retrofit.create(ProductService::class.java)

    suspend fun fetchProducts(
        page: Int,
        size: Int,
        category: String? = null,
    ): ProductsResponse =
        productService.requestProducts(
            page = page,
            size = size,
            category = category,
        )

    suspend fun fetchProductById(id: Long): ProductResponse = productService.requestProduct(id = id)
}
