package woowacourse.shopping.data.datasource.remote.producdetail

import retrofit2.Call
import woowacourse.shopping.data.remote.request.ProductDTO

interface ProductDetailSource {
    fun getById(id: Long): Call<ProductDTO>
}
