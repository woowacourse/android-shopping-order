package woowacourse.shopping.data.product

import ProductService
import retrofit2.Call
import woowacourse.shopping.data.ApiClient

object ProductServiceHelper : ProductRemoteDataSource {
    private val productService = ApiClient.client
        .create(ProductService::class.java)

    override fun getProductById(id: Int): Call<ProductDataModel> {
        return productService.getProductById(id)
    }

    override fun getAllProducts(): Call<List<ProductDataModel>> {
        return productService.getAllProducts()
    }
}
