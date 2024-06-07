package woowacourse.shopping.data.remote.source

import retrofit2.Response
import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.api.ProductApiService
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.data.source.ProductDataSource

class ProductDataSourceImpl(
    private val productApiService: ProductApiService = NetworkManager.productService(),
) : ProductDataSource {
    override suspend fun loadProducts(
        page: Int,
        size: Int,
    ): Response<ProductResponse> {
        return productApiService.requestProducts(
            page = page,
            size = size,
        )
    }

    override suspend fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Response<ProductResponse> {
        return productApiService.requestCategoryProducts(
            page = page,
            size = size,
            category = category,
        )
    }

    override suspend fun loadProduct(id: Int): Response<ProductDto> {
        return productApiService.requestProduct(id = id)
    }
}
