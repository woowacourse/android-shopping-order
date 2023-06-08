package woowacourse.shopping.data.datasource.remote.product

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.model.ProductDto

interface ProductService {
    @GET("/products")
    fun requestProducts(): Call<List<ProductDto>>
}
