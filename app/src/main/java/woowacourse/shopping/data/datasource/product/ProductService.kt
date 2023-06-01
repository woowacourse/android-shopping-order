package woowacourse.shopping.data.datasource.product

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.model.ProductEntity

interface ProductService {
    @GET("products")
    fun requestProducts(): Call<List<ProductEntity>>
}
