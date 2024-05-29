package woowacourse.shopping.data.remote.source

import retrofit2.Call
import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.api.ProductApiService
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.data.source.ProductDataSource
import javax.security.auth.callback.Callback

class ProductDataSourceImpl(
    private val productApiService: ProductApiService = NetworkManager.productService(),
) : ProductDataSource {
    override fun loadProducts(page: Int, size: Int): Call<ProductResponse> {
        return productApiService.requestProducts(
            page = page,
            size = size,
        )
    }

    override fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Call<ProductResponse> {
        return productApiService.requestCategoryProducts(
            page = page,
            size = size,
            category = category,
        )
    }

    override fun loadProduct(id: Int): Call<ProductDto> {
        return productApiService.requestProduct(id = id)
    }
}
