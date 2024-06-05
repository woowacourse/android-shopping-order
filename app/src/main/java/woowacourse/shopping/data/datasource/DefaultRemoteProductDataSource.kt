package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.remote.ProductService

class DefaultRemoteProductDataSource(private val productService: ProductService) :
    RemoteProductDataSource {
    override fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Call<ProductResponse> {
        return productService.getProducts(category, page, size, sort)
    }

    override fun getProductById(id: Int): Call<Product> {
        return productService.getProductById(id)
    }
}
