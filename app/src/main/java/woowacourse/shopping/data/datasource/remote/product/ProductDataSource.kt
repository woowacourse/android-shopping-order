package woowacourse.shopping.data.datasource.remote.product

import retrofit2.Call
import woowacourse.shopping.data.remote.request.ProductDTO

interface ProductDataSource {

    fun getAllProducts(): Call<List<ProductDTO>>
}
