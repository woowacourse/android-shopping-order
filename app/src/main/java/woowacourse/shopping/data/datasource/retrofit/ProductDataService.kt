package woowacourse.shopping.data.datasource.retrofit

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.remote.request.ProductDTO

interface ProductDataService {
    @GET("/products")
    fun getProducts(): Call<List<ProductDTO>>
}
