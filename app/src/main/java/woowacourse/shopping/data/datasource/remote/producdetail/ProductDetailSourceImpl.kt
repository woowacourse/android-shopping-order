package woowacourse.shopping.data.datasource.remote.producdetail

import retrofit2.Call
import woowacourse.shopping.data.datasource.retrofit.ServicePool
import woowacourse.shopping.data.remote.request.ProductDTO

class ProductDetailSourceImpl : ProductDetailSource {
    override fun getById(id: Long): Call<ProductDTO> {
        return ServicePool.productDetailService.getProductById(id)
    }
}
