package woowacourse.shopping.data.remote.datasource.product

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.ProductsResponse

interface ProductDataSource {
    suspend fun getProducts(
        category: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): Response<ProductsResponse>

    suspend fun postProduct(productRequest: ProductRequest): Response<Unit>

    suspend fun getProductById(id: Int): Response<ProductResponse>

    suspend fun deleteProductById(id: Int): Response<Unit>
}
