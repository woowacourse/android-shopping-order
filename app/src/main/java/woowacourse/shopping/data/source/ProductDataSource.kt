package woowacourse.shopping.data.source

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse

interface ProductDataSource {
    suspend fun loadProducts(
        page: Int,
        size: Int,
    ): Response<ProductResponse>

    suspend fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Response<ProductResponse>

    suspend fun loadProduct(id: Int): Response<ProductDto>
}
