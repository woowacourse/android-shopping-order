package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.model.product.ProductResponse
import woowacourse.shopping.data.service.ProductService

class ProductRemoteDataSourceImpl(
    private val productService: ProductService,
) : ProductRemoteDataSource {
    override fun fetchProducts(
        category: String?,
        page: Int,
        size: Int,
    ): Call<PageableResponse<ProductResponse>> = productService.fetchProducts(category, page, size)

    override fun fetchProduct(productId: Int): Call<ProductResponse> = productService.fetchProduct(productId)
}
