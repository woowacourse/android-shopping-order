package woowacourse.shopping.data.remote

import retrofit2.Call
import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductResponse

class RemoteProductDataSource(private val productService: ProductService) : ProductDataSource {
    override fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Call<ProductResponse> {
        return productService.getProducts(category, page, size, sort)
    }

    override fun getProductById(productId: Int): Call<ProductDto> {
        return productService.getProductById(productId)
    }
}
