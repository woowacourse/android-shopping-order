package woowacourse.shopping.data.product

import ProductService
import retrofit2.Call
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.model.BaseResponse

object ProductServiceHelper : ProductRemoteDataSource {
    private val productService = ApiClient.client
        .create(ProductService::class.java)

    override fun getProductById(id: Int): Call<BaseResponse<ProductDataModel>> {
        return productService.getProductById(id)
    }

    override fun getAllProducts(): Call<BaseResponse<List<ProductDataModel>>> {
        return productService.getAllProducts()
    }
}
